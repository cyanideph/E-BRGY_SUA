import { ID } from "node-appwrite";

const endpoint = process.env.APPWRITE_FUNCTION_API_ENDPOINT;
const project = process.env.APPWRITE_FUNCTION_PROJECT_ID;
const DATABASE_ID = process.env.EBRGY_DATABASE_ID;

if (!endpoint || !project || !DATABASE_ID) {
  throw new Error("Appwrite runtime configuration is missing.");
}

const T = {
  users: "users",
  residents: "residents",
  requests: "documentRequests",
  requestHistory: "requestStatusHistory",
  emergencies: "emergencyReports",
  emergencyHistory: "emergencyStatusHistory",
  notifications: "notifications",
  audit: "auditLogs",
  announcements: "announcements",
  events: "events"
};

function json(value) { return JSON.stringify(value); }
function now() { return new Date().toISOString(); }
function body(req) {
  try { return req.bodyJson ?? JSON.parse(req.body || "{}"); }
  catch { return {}; }
}
function userId(req) {
  return req.headers?.["x-appwrite-user-id"] || req.headers?.["X-Appwrite-User-Id"] || "";
}
function requireUser(req) {
  const uid = userId(req);
  if (!uid) throw new Error("Authenticated Appwrite user required.");
  return uid;
}
async function appwrite(req, path, method, payload) {
  const key = req.headers?.["x-appwrite-key"] || req.headers?.["X-Appwrite-Key"];
  if (!key) throw new Error("Appwrite Function runtime key unavailable.");
  const response = await fetch(endpoint + path, {
    method,
    headers: {
      "X-Appwrite-Project": project,
      "X-Appwrite-Key": key,
      "Content-Type": "application/json"
    },
    body: payload === undefined ? undefined : JSON.stringify(payload)
  });
  const text = await response.text();
  let data = {};
  try { data = text ? JSON.parse(text) : {}; } catch {}
  if (!response.ok) {
    throw new Error(data.message || data.error || text || `Appwrite HTTP ${response.status}`);
  }
  return data;
}
async function create(req, tableId, data, rowId = ID.unique(), permissions) {
  const payload = { rowId, data };
  if (permissions) payload.permissions = permissions;
  return appwrite(req, `/databases/${DATABASE_ID}/tables/${tableId}/rows`, "POST", payload);
}
async function get(req, tableId, rowId) {
  return appwrite(req, `/databases/${DATABASE_ID}/tables/${tableId}/rows/${rowId}`, "GET");
}
async function getUser(req, uid) {
  return appwrite(req, `/users/${uid}`, "GET");
}
async function update(req, tableId, rowId, data) {
  return appwrite(req, `/databases/${DATABASE_ID}/tables/${tableId}/rows/${rowId}`, "PATCH", { data });
}

async function notify(req, uid, title, message, type, referenceId = "") {
  return create(req, T.notifications, {
    userId: uid, title, body: message, type, read: false,
    createdAt: now(), priority: "Normal", referenceId
  }, ID.unique(), [`read("user:${uid}")`]);
}
async function audit(req, uid, action, resourceType, resourceId, details) {
  return create(req, T.audit, {
    actorUserId: uid, action, resourceType, resourceId, details, createdAt: now()
  });
}

async function handle(req) {
  const route = req.path || "/";
  if (route === "/health") return { ok: true, service: "ebrgyauthority", version: "1.0.0" };

  const uid = requireUser(req);
  const input = body(req);
  
  if (route === "/register-resident" && req.method === "POST") {
    const required = ["fullName", "email", "address", "mobileNumber"];
    for (const k of required) if (!input[k]) throw new Error(`Missing ${k}`);

    let existing = null;
    try { existing = await get(req, T.residents, uid); } catch {}
    if (existing) throw new Error("Resident profile already exists.");

    const accountUser = await getUser(req, uid);
    const accountEmail = String(accountUser.email || "").trim();
    if (!accountEmail || accountEmail.toLowerCase() !== String(input.email).trim().toLowerCase()) {
      throw new Error("Registration email does not match the authenticated Appwrite account.");
    }

    const createdAt = now();
    const ownerPermission = [`read("user:${uid}")`];

    await create(req, T.users, {
      userId: uid,
      name: String(input.fullName),
      email: accountEmail,
      role: "resident",
      address: String(input.address),
      phone: String(input.mobileNumber),
      createdAt
    }, uid, ownerPermission);

    try {
      await create(req, T.residents, {
        userId: uid,
        fullName: String(input.fullName),
        address: String(input.address),
        mobileNumber: String(input.mobileNumber),
        birthDate: input.dateOfBirth || null,
        civilStatus: String(input.civilStatus || ""),
        occupation: String(input.occupation || ""),
        emergencyContactName: String(input.emergencyContactName || ""),
        emergencyContactRelationship: String(input.emergencyContactRelationship || ""),
        emergencyContactPhone: String(input.emergencyContactPhone || ""),
        latitude: input.latitude ?? null,
        longitude: input.longitude ?? null,
        residentId: uid,
        verified: false,
        registrationStatus: "Pending Verification",
        createdAt
      }, uid, ownerPermission);
    } catch (e) {
      try { await appwrite(req, `/databases/${DATABASE_ID}/tables/${T.users}/rows/${uid}`, "DELETE"); } catch {}
      throw e;
    }

    await audit(req, uid, "REGISTER_RESIDENT", "Resident", uid, "Resident profile created");
    return { ok: true, residentId: uid, registrationStatus: "Pending Verification" };
  }

  if (route === "/request" && req.method === "POST") {
    const required = ["requestId","serviceId","referenceNumber","details"];
    for (const k of required) if (!input[k]) throw new Error(`Missing ${k}`);
    const row = await create(req, T.requests, {
      userId: uid,
      serviceId: String(input.serviceId),
      referenceNumber: String(input.referenceNumber),
      status: "Submitted",
      details: String(input.details),
      submittedAt: now(),
      updatedAt: now()
    }, String(input.requestId).slice(0, 36), [`read("user:${uid}")`]);
    await create(req, T.requestHistory, {
      requestId: row.$id, status: "Submitted", remarks: "", changedBy: uid, changedAt: now()
    });
    await notify(req, uid, "Request Submitted", `Request ${input.referenceNumber} was received by the barangay.`, "Service Request", input.referenceNumber);
    await audit(req, uid, "CREATE_REQUEST", "DocumentRequest", row.$id, String(input.referenceNumber));
    return { ok: true, requestId: row.$id };
  }

  if (route === "/request-status" && req.method === "POST") {
    const requestId = String(input.requestId || "");
    const status = String(input.status || "");
    if (!requestId || !status) throw new Error("requestId and status are required.");
    const request = await get(req, T.requests, requestId);
    const actor = await get(req, T.users, uid);
    const role = String(actor.role || "");
    if (!["staff","official","admin"].includes(role)) throw new Error("Staff authorization required.");
    await update(req, T.requests, requestId, { status, updatedAt: now() });
    await create(req, T.requestHistory, {
      requestId, status, remarks: String(input.remarks || ""), changedBy: uid, changedAt: now()
    });
    const resident = String(request.userId || "");
    await notify(req, resident, "Request Status Updated", `Your request status is now ${status}.`, "Service Request", String(request.referenceNumber || ""));
    await audit(req, uid, "UPDATE_REQUEST_STATUS", "DocumentRequest", requestId, status);
    return { ok: true };
  }

  if (route === "/emergency" && req.method === "POST") {
    const id = String(input.reportId || ID.unique()).slice(0, 36);
    const type = String(input.type || "Barangay Emergency");
    const row = await create(req, T.emergencies, {
      userId: uid, type, description: String(input.description || ""),
      latitude: input.latitude ?? null, longitude: input.longitude ?? null,
      status: "Reported", createdAt: now()
    }, id, [`read("user:${uid}")`]);
    await create(req, T.emergencyHistory, {
      reportId: id, status: "Reported", responder: "", notes: "", changedBy: uid, changedAt: now()
    });
    await notify(req, uid, "Emergency Report Accepted", `Emergency report ${id} was accepted by the barangay backend.`, "Emergency", id);
    await audit(req, uid, "EMERGENCY_SOS", "EmergencyReport", id, type);
    return { ok: true, reportId: id };
  }

  if (route === "/emergency-status" && req.method === "POST") {
    const reportId = String(input.reportId || "");
    const status = String(input.status || "");
    if (!reportId || !status) throw new Error("reportId and status are required.");
    const actor = await get(req, T.users, uid);
    const role = String(actor.role || "");
    if (!["staff","official","admin"].includes(role)) throw new Error("Responder authorization required.");
    const report = await get(req, T.emergencies, reportId);
    await update(req, T.emergencies, reportId, { status });
    await create(req, T.emergencyHistory, {
      reportId, status, responder: String(input.responder || ""), notes: String(input.notes || ""),
      changedBy: uid, changedAt: now()
    });
    const resident = String(report.userId || "");
    await notify(req, resident, "Emergency Status Updated", `Your emergency report status is now ${status}.`, "Emergency", reportId);
    await audit(req, uid, "UPDATE_EMERGENCY_STATUS", "EmergencyReport", reportId, status);
    return { ok: true };
  }

  if (route === "/announcement" && req.method === "POST") {
    const actor = await get(req, T.users, uid);
    if (!["official","admin"].includes(String(actor.role || ""))) throw new Error("Official authorization required.");
    const created = await create(req, T.announcements, {
      title: String(input.title || ""), body: String(input.description || ""),
      published: true, publishedAt: now(), createdAt: now(),
      category: String(input.category || "General"), priority: String(input.priority || "Normal"),
      authorName: String(actor.name || input.authorName || ""), authorRole: String(actor.role || ""), isPinned: Boolean(input.isPinned)
    });
    await audit(req, uid, "CREATE_ANNOUNCEMENT", "Announcement", created.$id, String(input.title || ""));
    return { ok: true, announcementId: created.$id };
  }

  if (route === "/event" && req.method === "POST") {
    const actor = await get(req, T.users, uid);
    if (!["official","admin"].includes(String(actor.role || ""))) throw new Error("Official authorization required.");
    const created = await create(req, T.events, {
      title: String(input.title || ""), description: String(input.description || ""),
      startsAt: String(input.startsAt || ""), endsAt: String(input.endsAt || ""),
      location: String(input.location || ""), createdAt: now(),
      organizer: String(input.organizer || ""), category: String(input.category || ""), rsvpCount: 0
    });
    await audit(req, uid, "CREATE_EVENT", "Event", created.$id, String(input.title || ""));
    return { ok: true, eventId: created.$id };
  }

  throw new Error("Unsupported backend-authority route.");
}

export default async ({ req, res, error }) => {
  try { return res.json(await handle(req)); }
  catch (e) {
    error(e?.stack || String(e));
    return res.json({ ok: false, error: e?.message || "Backend operation failed." }, 400);
  }
};
