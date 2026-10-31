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

const STAFF_ROLES = ["staff", "official", "admin"];
const OFFICIAL_ROLES = ["official", "admin"];
const REQUEST_STATUSES = ["Submitted", "Under Review", "Processing", "Ready", "Completed", "Rejected"];
const EMERGENCY_STATUSES = ["Reported", "Acknowledged", "Responding", "Resolved", "Cancelled"];
const ID_PATTERN = /^[A-Za-z0-9_-]+$/;
const MAX_ID_LENGTH = 36;

// Field level input caps (audit item 5). Unknown fields are ignored.
const LIMITS = {
  remarks: 2000,
  notes: 2000,
  details: 2000,
  description: 2000,
  body: 2000,
  title: 255,
  status: 40,
  responder: 160,
  serviceId: 36,
  referenceNumber: 32,
  name: 255,
  fullName: 255,
  authorName: 255,
  mobile: 32,
  mobileNumber: 32,
  category: 64,
  priority: 32,
  address: 2000
};

// Best-effort, per-uid, in-memory rate limiting (audit item 7).
const RATE_LIMITS = {
  "/emergency": 5,
  "/request": 30,
  "/announcement": 5,
  "/event": 5
};
const RATE_WINDOW_MS = 60 * 1000;
const RATE_MAX_KEYS = 1000;
const rateBuckets = new Map();

class HttpError extends Error {
  constructor(status, code, message) {
    super(message);
    this.name = "HttpError";
    this.status = status;
    this.code = code;
  }
}
function fail(status, code, message) {
  return new HttpError(status, code, message);
}

function json(value) { return JSON.stringify(value); }
function now() { return new Date().toISOString(); }
function body(req) {
  try { return req.bodyJson ?? JSON.parse(req.body || "{}"); }
  catch { return {}; }
}
function header(req, name) {
  const headers = req?.headers || {};
  const lower = name.toLowerCase();
  if (headers[name] !== undefined && headers[name] !== null) return headers[name];
  if (headers[lower] !== undefined && headers[lower] !== null) return headers[lower];
  for (const key of Object.keys(headers)) {
    if (key.toLowerCase() === lower) return headers[key];
  }
  return undefined;
}
function userId(req) {
  return String(header(req, "x-appwrite-user-id") || "");
}
function requireUser(req) {
  const uid = userId(req);
  if (!uid) throw fail(401, "UNAUTHENTICATED", "Authenticated Appwrite user required.");
  return uid;
}
function requireJwt(req) {
  return String(header(req, "x-appwrite-user-jwt") || "").trim();
}

// --- validation helpers (audit item 5): all failures are generic JSON errors ---
function assertLength(value, field) {
  const max = LIMITS[field];
  if (max === undefined || value === undefined || value === null) return value;
  if (String(value).length > max) {
    throw fail(400, "BAD_REQUEST", `${field} must be at most ${max} characters.`);
  }
  return value;
}
function assertId(value, field) {
  const id = String(value ?? "").trim();
  if (!id || id.length > MAX_ID_LENGTH || !ID_PATTERN.test(id)) {
    throw fail(400, "BAD_REQUEST", `${field} must be 1-${MAX_ID_LENGTH} characters using only letters, numbers, "-" or "_".`);
  }
  return id;
}
function assertEnum(value, allowed, field) {
  if (!allowed.includes(value)) {
    throw fail(400, "BAD_REQUEST", `${field} must be one of: ${allowed.join(", ")}.`);
  }
  return value;
}
function requireFields(input, fields) {
  for (const field of fields) if (!input[field]) throw fail(400, "BAD_REQUEST", `Missing ${field}`);
}

// --- rate limiting (audit item 7) ---
function enforceRateLimit(route, uid) {
  const limit = RATE_LIMITS[route];
  if (!limit) return;
  const ts = Date.now();
  // Lazy cleanup of expired windows so memory stays bounded.
  for (const [key, bucket] of rateBuckets) {
    if (ts - bucket.startedAt >= RATE_WINDOW_MS) rateBuckets.delete(key);
  }
  const key = `${route}:${uid}`;
  let bucket = rateBuckets.get(key);
  if (!bucket) {
    bucket = { startedAt: ts, count: 0 };
    rateBuckets.set(key, bucket);
    // Cap the map size: evict the oldest entries first (insertion order).
    for (const oldest of rateBuckets.keys()) {
      if (rateBuckets.size <= RATE_MAX_KEYS) break;
      rateBuckets.delete(oldest);
    }
  }
  bucket.count += 1;
  if (bucket.count > limit) {
    throw fail(429, "RATE_LIMITED", "Too many requests. Please try again in a minute.");
  }
}

async function appwrite(req, path, method, payload) {
  const key = String(header(req, "x-appwrite-key") || "");
  if (!key) {
    console.error(`[ebrgyauthority] Appwrite runtime key unavailable for ${method} ${path}.`);
    throw fail(400, "SERVER_ERROR", "Backend configuration error.");
  }
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
    // Real detail is logged only, never returned to the caller (audit item 1).
    const detail = data.message || data.error || text || `Appwrite HTTP ${response.status}`;
    console.error(`[ebrgyauthority] Appwrite ${method} ${path} failed (${response.status}): ${detail}`);
    if (response.status >= 500) throw fail(400, "SERVER_ERROR", "The backend could not complete the operation.");
    throw fail(400, "BAD_REQUEST", "The backend could not complete the operation.");
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

// Opportunistic caller <-> JWT binding (audit item 2). No x-appwrite-key is sent.
async function assertJwtMatchesUser(req, uid) {
  const token = requireJwt(req);
  if (!token) return; // console / CI / admin triggered executions have no JWT
  try {
    const response = await fetch(endpoint + "/account", {
      method: "GET",
      headers: {
        "X-Appwrite-Project": project,
        Authorization: `Bearer ${token}`
      }
    });
    const text = await response.text();
    let account = {};
    try { account = text ? JSON.parse(text) : {}; } catch {}
    if (!response.ok || String(account.$id || "") !== uid) {
      console.error(`[ebrgyauthority] JWT verification rejected (http ${response.status}, token subject ${account.$id || "none"}, header user ${uid || "none"}).`);
      throw fail(401, "UNAUTHENTICATED", "Invalid or expired session token.");
    }
  } catch (e) {
    if (e instanceof HttpError) throw e;
    console.error(`[ebrgyauthority] JWT verification failed: ${e?.stack || e}`);
    throw fail(401, "UNAUTHENTICATED", "Invalid or expired session token.");
  }
}

// Role resolution with generic failure (audit item 3).
async function requireRole(req, uid, roles, deniedMessage) {
  let actor;
  try {
    actor = await get(req, T.users, uid);
  } catch (e) {
    // Missing users row (or any upstream failure): never leak Appwrite text.
    console.error(`[ebrgyauthority] Role lookup failed for user ${uid}: ${e?.stack || e}`);
    throw fail(401, "UNAUTHENTICATED", "Unable to verify the requesting user.");
  }
  const role = String(actor?.role || "");
  if (!roles.includes(role)) throw fail(403, "FORBIDDEN", deniedMessage);
  return actor;
}

function ownerPermissions(ownerUid) {
  return ownerUid ? [`read("user:${ownerUid}")`] : [];
}

async function handle(req) {
  const route = req.path || "/";
  if (route === "/health") return { ok: true, service: "ebrgyauthority", version: "1.0.0" };

  const uid = requireUser(req);
  await assertJwtMatchesUser(req, uid);
  enforceRateLimit(route, uid);

  const input = body(req);

if (route === "/first-time-jobseeker-clearance" && req.method === "POST") {
    requireFields(input, ["applicantName", "course", "graduationDate", "employmentStatus", "barcodeNumber"]);
    assertLength(input.applicantName, "applicantName");
    assertLength(input.course, "course");
    assertLength(input.graduationDate, "graduationDate");
    assertLength(input.employmentStatus, "employmentStatus");
    assertLength(input.barcodeNumber, "barcodeNumber");

    const uid = requireUser(req);
    const existing = null;
    // Check if resident already exists
    try { existing = await get(req, T.residents, uid); } catch {}

    if (existing) throw fail(400, "BAD_REQUEST", "Resident profile already exists.");

    const accountUser = await getUser(req, uid);
    const accountEmail = String(accountUser.email || "").trim();
    if (!accountEmail || accountEmail.toLowerCase() !== String(input.applicantName).trim().toLowerCase().split(" ").at(-1)) {
      throw fail(400, "BAD_REQUEST", "Application email does not match the authenticated Appwrite account.");
    }

    const createdAt = now();
    const ownerPermission = [`read("user:${uid}")`];

    await create(req, T.residents, {
      userId: uid,
      fullName: String(input.applicantName),
      address: "", // will be filled later
      mobileNumber: "", // will be filled later
      birthDate: input.graduationDate || null,
      civilStatus: "",
      occupation: "First-Time Jobseeker",
      emergencyContactName: "",
      emergencyContactRelationship: "",
      emergencyContactPhone: "",
      latitude: null,
      longitude: null,
      residentId: uid,
      verified: false,
      registrationStatus: "Pending Verification",
      createdAt
    }, uid, ownerPermission);

    await audit(req, uid, "FIRST_TIME_JOBSEEKER_CLEARANCE", "Resident", uid, `Applicant: ${input.applicantName}`);

    return { ok: true, residentId: uid, registrationStatus: "Pending Verification" };
  }

  if (route === "/register-resident" && req.method === "POST") {
    requireFields(input, ["fullName", "email", "address", "mobileNumber"]);
    assertLength(input.fullName, "fullName");
    assertLength(input.address, "address");
    assertLength(input.mobileNumber, "mobileNumber");

    let existing = null;
    try { existing = await get(req, T.residents, uid); } catch {}
    if (existing) throw fail(400, "BAD_REQUEST", "Resident profile already exists.");

    const accountUser = await getUser(req, uid);
    const accountEmail = String(accountUser.email || "").trim();
    if (!accountEmail || accountEmail.toLowerCase() !== String(input.email).trim().toLowerCase()) {
      throw fail(400, "BAD_REQUEST", "Registration email does not match the authenticated Appwrite account.");
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
    requireFields(input, ["requestId", "serviceId", "referenceNumber", "details"]);
    const requestId = assertId(input.requestId, "requestId");
    assertLength(input.serviceId, "serviceId");
    assertLength(input.referenceNumber, "referenceNumber");
    assertLength(input.details, "details");

    const row = await create(req, T.requests, {
      userId: uid,
      serviceId: String(input.serviceId),
      referenceNumber: String(input.referenceNumber),
      status: "Submitted",
      details: String(input.details),
      submittedAt: now(),
      updatedAt: now()
    }, requestId, ownerPermissions(uid));
    await create(req, T.requestHistory, {
      requestId: row.$id, status: "Submitted", remarks: "", changedBy: uid, changedAt: now()
    }, ID.unique(), ownerPermissions(uid));
    await notify(req, uid, "Request Submitted", `Request ${input.referenceNumber} was received by the barangay.`, "Service Request", input.referenceNumber);
    await audit(req, uid, "CREATE_REQUEST", "DocumentRequest", row.$id, String(input.referenceNumber));
    return { ok: true, requestId: row.$id };
  }

  if (route === "/request-status" && req.method === "POST") {
    const requestId = String(input.requestId ?? "").trim();
    const status = String(input.status ?? "").trim();
    if (!requestId || !status) throw fail(400, "BAD_REQUEST", "requestId and status are required.");
    assertId(requestId, "requestId");
    assertEnum(status, REQUEST_STATUSES, "status");
    assertLength(input.remarks, "remarks");

    // Role check happens before the target resource is fetched (audit item 3).
    await requireRole(req, uid, STAFF_ROLES, "Staff authorization required.");
    const request = await get(req, T.requests, requestId);

    await update(req, T.requests, requestId, { status, updatedAt: now() });
    await create(req, T.requestHistory, {
      requestId, status, remarks: String(input.remarks || ""), changedBy: uid, changedAt: now()
    }, ID.unique(), ownerPermissions(String(request.userId || "")));
    const resident = String(request.userId || "");
    await notify(req, resident, "Request Status Updated", `Your request status is now ${status}.`, "Service Request", String(request.referenceNumber || ""));
    await audit(req, uid, "UPDATE_REQUEST_STATUS", "DocumentRequest", requestId, status);
    return { ok: true };
  }

  if (route === "/philsys-lookup" && req.method === "POST") {
    const input = body(req);
    requireFields(input, ["philId"]);
    assertLength(input.philId, "philId");

    const uid = requireUser(req);

    // Simulate PhilSys lookup - in production this would call the official PH PhilSys API
    const philId = String(input.philId).trim();
    if (philId.length < 12 || philId.length > 15) {
      throw fail(400, "BAD_REQUEST", "PhilSys ID must be 12-15 digits.");
    }

    // Mock lookup - would integrate with official PH government API
    const mockRecord: any = {
      philId: philId,
      fullName: "Mock Resident",
      dateOfBirth: "1990-01-01",
      address: "Barangay Sua, San Juan, Southern Leyte",
      gender: "Male",
      civilStatus: "Single",
      bloodType: "O+",
      dateIssued: now(),
      expiryDate: "2030-01-01"
    };

    await create(req, T.audit, {
      actorUserId: uid, action: "LOOKUP_PHILSYS", resourceType: "PhilSys",
      resourceId: philId, details: JSON.stringify({philId, found: true})
    });

    return { ok: true, record: mockRecord };
  }

  if (route === "/tanod-patrol" && req.method === "POST") {
    const input = body(req);
    requireFields(input, ["officerName", "patrolArea", "status"]);
    assertLength(input.officerName, "officerName");
    assertLength(input.patrolArea, "patrolArea");
    assertLength(input.status, "status");

    const uid = requireUser(req);
    const report = await create(req, T.emergencies, {
      userId: uid, type: "Tanod Patrol", description: input.patrolArea,
      latitude: input.latitude ?? null, longitude: input.longitude ?? null,
      status: String(input.status || "Active"),
      createdAt: now()
    }, ID.unique(), ownerPermissions(uid));
    await create(req, T.emergencyHistory, {
      reportId: report.$id, status: "Active", responder: "", notes: String(input.notes || ""),
      changedBy: uid, changedAt: now()
    }, ID.unique(), ownerPermissions(uid));
    await notify(req, uid, "Tanod Patrol Report", `Patrol report was accepted by the barangay backend.`, "Emergency", report.$id);
    await audit(req, uid, "CREATE_EMERGENCY", "EmergencyReport", report.$id, "Tanod Patrol");
    return { ok: true, reportId: report.$id };
  }

  if (route === "/emergency" && req.method === "POST") {
    const rawReportId = String(input.reportId ?? "").trim();
    const id = rawReportId ? assertId(rawReportId, "reportId") : ID.unique();
    assertLength(input.description, "description");
    const type = String(input.type || "Barangay Emergency");

    const row = await create(req, T.emergencies, {
      userId: uid, type, description: String(input.description || ""),
      latitude: input.latitude ?? null, longitude: input.longitude ?? null,
      status: "Reported", createdAt: now()
    }, id, ownerPermissions(uid));
    await create(req, T.emergencyHistory, {
      reportId: id, status: "Reported", responder: "", notes: "", changedBy: uid, changedAt: now()
    }, ID.unique(), ownerPermissions(uid));
    await notify(req, uid, "Emergency Report Accepted", `Emergency report ${id} was accepted by the barangay backend.`, "Emergency", id);
    await audit(req, uid, "EMERGENCY_SOS", "EmergencyReport", id, type);
    return { ok: true, reportId: id };
  }

  if (route === "/emergency-status" && req.method === "POST") {
    const reportId = String(input.reportId ?? "").trim();
    const status = String(input.status ?? "").trim();
    if (!reportId || !status) throw fail(400, "BAD_REQUEST", "reportId and status are required.");
    assertId(reportId, "reportId");
    assertEnum(status, EMERGENCY_STATUSES, "status");
    assertLength(input.responder, "responder");
    assertLength(input.notes, "notes");

    // Role check happens before the target resource is fetched (audit item 3).
    await requireRole(req, uid, STAFF_ROLES, "Responder authorization required.");
    const report = await get(req, T.emergencies, reportId);

    await update(req, T.emergencies, reportId, { status });
    await create(req, T.emergencyHistory, {
      reportId, status, responder: String(input.responder || ""), notes: String(input.notes || ""),
      changedBy: uid, changedAt: now()
    }, ID.unique(), ownerPermissions(String(report.userId || "")));
    const resident = String(report.userId || "");
    await notify(req, resident, "Emergency Status Updated", `Your emergency report status is now ${status}.`, "Emergency", reportId);
    await audit(req, uid, "UPDATE_EMERGENCY_STATUS", "EmergencyReport", reportId, status);
    return { ok: true };
  }

  if (route === "/financial-report" && req.method === "POST") {
    const actor = await requireRole(req, uid, OFFICIAL_ROLES, "Official authorization required.");

    const reportType = String(input.reportType ?? "");
    const description = String(input.description ?? "");
    const amount = Number(input.amount) || 0;
    const category = String(input.category ?? "General");
    const preparedBy = String(input.preparedBy ?? "");

    if (!reportType.trim() || !description.trim()) {
      throw fail(400, "BAD_REQUEST", "reportType and description are required.");
    }
    assertLength(reportType, "reportType");
    assertLength(description, "description");
    assertLength(input.body, "body");
    assertLength(input.category, "category");

    const created = await create(req, T.audit, {
      actorUserId: uid, action: "CREATE_FINANCIAL_REPORT", resourceType: "FinancialReport",
      resourceId: ID.unique(), details: JSON.stringify({reportType, description, amount, category, preparedBy})
    });

    await create(req, T.audit, {
      actorUserId: uid, action: "CREATE_FINANCIAL_REPORT", resourceType: "FinancialReport",
      resourceId: created.$id, details: JSON.stringify({reportType, description, amount, category, preparedBy})
    });

    return { ok: true, reportId: created.$id };
  }

  if (route === "/event-voting" && req.method === "POST") {
    const input = body(req);
    requireFields(input, ["eventId", "open"]);
    assertLength(input.eventId, "eventId");
    assertBoolean(input.open, "open");

    const actor = await requireRole(req, uid, OFFICIAL_ROLES, "Official authorization required.");
    const event = await get(req, T.events, input.eventId);

    await update(req, T.events, input.eventId, {
      title: event.title, description: event.description,
      startsAt: event.startsAt, endsAt: event.endsAt,
      location: event.location, organizer: event.organizer,
      category: event.category, rsvpCount: event.rsvpCount,
      votingOpen: Boolean(input.open), yesVotes: event.yesVotes, noVotes: event.noVotes
    });

    await audit(req, uid, "TOGGLE_EVENT_VOTING", "Event", input.eventId, `Voting ${Boolean(input.open) ? "opened" : "closed"}`);
    return { ok: true, votingOpen: Boolean(input.open) };
  }

  if (route === "/announcement" && req.method === "POST") {
    const actor = await requireRole(req, uid, OFFICIAL_ROLES, "Official authorization required.");

    const title = String(input.title ?? "");
    const announcementBody = String(input.description ?? "");
    if (!title.trim() || !announcementBody.trim()) {
      throw fail(400, "BAD_REQUEST", "title and body are required.");
    }
    assertLength(title, "title");
    assertLength(announcementBody, "body");
    assertLength(input.body, "body");
    assertLength(input.category, "category");
    assertLength(input.priority, "priority");

    const created = await create(req, T.announcements, {
      title, body: announcementBody,
      published: true, publishedAt: now(), createdAt: now(),
      category: String(input.category || "General"), priority: String(input.priority || "Normal"),
      authorName: String(actor.name || ""), authorRole: String(actor.role || ""), isPinned: Boolean(input.isPinned)
    });
    await audit(req, uid, "CREATE_ANNOUNCEMENT", "Announcement", created.$id, title);
    return { ok: true, announcementId: created.$id };
  }

  if (route === "/event" && req.method === "POST") {
    await requireRole(req, uid, OFFICIAL_ROLES, "Official authorization required.");
    assertLength(input.title, "title");
    assertLength(input.description, "description");
    assertLength(input.category, "category");

    const created = await create(req, T.events, {
      title: String(input.title || ""), description: String(input.description || ""),
      startsAt: String(input.startsAt || ""), endsAt: String(input.endsAt || ""),
      location: String(input.location || ""), createdAt: now(),
      organizer: String(input.organizer || ""), category: String(input.category || ""), rsvpCount: 0
    });
    await audit(req, uid, "CREATE_EVENT", "Event", created.$id, String(input.title || ""));
    return { ok: true, eventId: created.$id };
  }

  throw fail(404, "NOT_FOUND", "Unsupported backend-authority route.");
}

export default async ({ req, res, error }) => {
  try { return res.json(await handle(req)); }
  catch (e) {
    const isHttp = e instanceof HttpError;
    const status = isHttp ? e.status : 400;
    const code = isHttp ? e.code : "SERVER_ERROR";
    const message = isHttp && e.message ? e.message : "The backend could not complete the operation.";
    // Real detail (stack / upstream text) stays in the logs only.
    console.error(`[ebrgyauthority] ${code} (${status}): ${e?.stack || String(e)}`);
    try { if (typeof error === "function") error(`${code}: ${message}`); } catch {}
    return res.json({ ok: false, code, message }, status);
  }
};
