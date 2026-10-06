import { Client, TablesDB, ID } from "node-appwrite";

const endpoint = "https://sgp.cloud.appwrite.io/v1";
const project = "6ac31e4000390af0f850";


function dbFor(req) {
  const key = req.headers?.["x-appwrite-key"] || req.headers?.["X-Appwrite-Key"] || process.env.APPWRITE_FUNCTION_API_KEY;
  const client = new Client().setEndpoint(endpoint).setProject(project).setKey(key);
  return new TablesDB(client);
}
const DATABASE_ID = process.env.APPWRITE_DATABASE_ID || "ebarangay-sua-db";

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
async function create(db, tableId, data, rowId = ID.unique(), permissions) {
  return db.createRow(DATABASE_ID, tableId, rowId, data, permissions);
}
async function notify(db, uid, title, message, type, referenceId = "") {
  return create(db, T.notifications, {
    userId: uid, title, body: message, type, read: false,
    createdAt: now(), priority: "Normal", referenceId
  }, ID.unique(), [`read("user:${uid}")`]);
}
async function audit(db, uid, action, resourceType, resourceId, details) {
  return create(db, T.audit, {
    actorUserId: uid, action, resourceType, resourceId, details, createdAt: now()
  });
}

async function handle(req) {
  const route = req.path || "/";
  if (route === "/health") return { ok: true, service: "ebrgyauthority", version: "1.0.0" };

  const uid = requireUser(req);
  const input = body(req);
  const db = dbFor(req);

  if (route === "/request" && req.method === "POST") {
    const required = ["requestId","serviceId","referenceNumber","details"];
    for (const k of required) if (!input[k]) throw new Error(`Missing ${k}`);
    const row = await create(db, T.requests, {
      userId: uid,
      serviceId: String(input.serviceId),
      referenceNumber: String(input.referenceNumber),
      status: "Submitted",
      details: String(input.details),
      submittedAt: now(),
      updatedAt: now()
    }, String(input.requestId).slice(0, 36), [`read("user:${uid}")`]);
    await create(T.requestHistory, {
      requestId: row.$id, status: "Submitted", remarks: "", changedBy: uid, changedAt: now()
    });
    await notify(db, uid, "Request Submitted", `Request ${input.referenceNumber} was received by the barangay.`, "Service Request", input.referenceNumber);
    await audit(db, uid, "CREATE_REQUEST", "DocumentRequest", row.$id, String(input.referenceNumber));
    return { ok: true, requestId: row.$id };
  }

  if (route === "/request-status" && req.method === "POST") {
    const requestId = String(input.requestId || "");
    const status = String(input.status || "");
    if (!requestId || !status) throw new Error("requestId and status are required.");
    const request = await db.getRow(DATABASE_ID, T.requests, requestId);
    const actor = await db.getRow(DATABASE_ID, T.users, uid);
    const role = String(actor.role || "");
    if (!["staff","official","admin"].includes(role)) throw new Error("Staff authorization required.");
    await db.updateRow(DATABASE_ID, T.requests, requestId, { status, updatedAt: now() });
    await create(T.requestHistory, {
      requestId, status, remarks: String(input.remarks || ""), changedBy: uid, changedAt: now()
    });
    const resident = String(request.userId || "");
    await notify(resident, "Request Status Updated", `Your request status is now ${status}.`, "Service Request", String(request.referenceNumber || ""));
    await audit(uid, "UPDATE_REQUEST_STATUS", "DocumentRequest", requestId, status);
    return { ok: true };
  }

  if (route === "/emergency" && req.method === "POST") {
    const id = String(input.reportId || ID.unique()).slice(0, 36);
    const type = String(input.type || "Barangay Emergency");
    const row = await create(T.emergencies, {
      userId: uid, type, description: String(input.description || ""),
      latitude: input.latitude ?? null, longitude: input.longitude ?? null,
      status: "Reported", createdAt: now()
    }, id, [`read("user:${uid}")`]);
    await create(T.emergencyHistory, {
      reportId: id, status: "Reported", responder: "", notes: "", changedBy: uid, changedAt: now()
    });
    await notify(uid, "Emergency Report Accepted", `Emergency report ${id} was accepted by the barangay backend.`, "Emergency", id);
    await audit(uid, "EMERGENCY_SOS", "EmergencyReport", id, type);
    return { ok: true, reportId: id };
  }

  if (route === "/emergency-status" && req.method === "POST") {
    const reportId = String(input.reportId || "");
    const status = String(input.status || "");
    if (!reportId || !status) throw new Error("reportId and status are required.");
    const actor = await db.getDocument(DATABASE_ID, T.users, uid);
    const role = String(actor.role || "");
    if (!["staff","official","admin"].includes(role)) throw new Error("Responder authorization required.");
    const report = await db.getRow(DATABASE_ID, T.emergencies, reportId);
    await db.updateRow(DATABASE_ID, T.emergencies, reportId, { status });
    await create(T.emergencyHistory, {
      reportId, status, responder: String(input.responder || ""), notes: String(input.notes || ""),
      changedBy: uid, changedAt: now()
    });
    const resident = String(report.userId || "");
    await notify(resident, "Emergency Status Updated", `Your emergency report status is now ${status}.`, "Emergency", reportId);
    await audit(uid, "UPDATE_EMERGENCY_STATUS", "EmergencyReport", reportId, status);
    return { ok: true };
  }

  if (route === "/announcement" && req.method === "POST") {
    const actor = await db.getDocument(DATABASE_ID, T.users, uid);
    if (!["official","admin"].includes(String(actor.role || ""))) throw new Error("Official authorization required.");
    const created = await create(T.announcements, {
      title: String(input.title || ""), body: String(input.description || ""),
      published: true, publishedAt: now(), createdAt: now(),
      category: String(input.category || "General"), priority: String(input.priority || "Normal"),
      authorName: String(input.authorName || ""), authorRole: String(actor.role || ""), isPinned: Boolean(input.isPinned)
    });
    await audit(uid, "CREATE_ANNOUNCEMENT", "Announcement", created.$id, String(input.title || ""));
    return { ok: true, announcementId: created.$id };
  }

  if (route === "/event" && req.method === "POST") {
    const actor = await db.getDocument(DATABASE_ID, T.users, uid);
    if (!["official","admin"].includes(String(actor.role || ""))) throw new Error("Official authorization required.");
    const created = await create(T.events, {
      title: String(input.title || ""), description: String(input.description || ""),
      startsAt: String(input.startsAt || ""), endsAt: String(input.endsAt || ""),
      location: String(input.location || ""), createdAt: now(),
      organizer: String(input.organizer || ""), category: String(input.category || ""), rsvpCount: 0
    });
    await audit(uid, "CREATE_EVENT", "Event", created.$id, String(input.title || ""));
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
