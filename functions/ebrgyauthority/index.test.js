import test from "node:test";
import assert from "node:assert/strict";

process.env.APPWRITE_FUNCTION_API_ENDPOINT = "https://sgp.cloud.appwrite.io/v1";
process.env.APPWRITE_FUNCTION_PROJECT_ID = "test-project";
process.env.EBRGY_DATABASE_ID = "test-db";

const { default: handler } = await import("./index.js");

function invoke({ path, method = "POST", headers = {}, bodyJson = {} }) {
  let response;
  const errors = [];
  return handler({
    req: { path, method, headers, bodyJson },
    res: {
      json(value, status = 200) {
        response = { value, status };
        return response;
      }
    },
    error(value) {
      errors.push(String(value));
    }
  }).then(() => ({ response, errors }));
}

function ok(value = {}, status = 200) {
  return new Response(JSON.stringify(value), { status });
}

test("health endpoint is public and returns 200", async () => {
  const result = await invoke({ path: "/health", method: "GET" });
  assert.equal(result.response.status, 200);
  assert.equal(result.response.value.ok, true);
});

test("mutating routes require an authenticated Appwrite user", async () => {
  const result = await invoke({
    path: "/request",
    bodyJson: {
      requestId: "request-1",
      serviceId: "clearance",
      referenceNumber: "REF-1",
      details: "test"
    }
  });
  assert.equal(result.response.status, 401);
  assert.equal(result.response.value.ok, false);
  assert.equal(result.response.value.code, "UNAUTHENTICATED");
  assert.match(result.response.value.message, /Authenticated Appwrite user required/);
  assert.ok(!("error" in result.response.value));
});

test("resident cannot change request status", async () => {
  globalThis.fetch = async (url) => {
    if (String(url).endsWith("/databases/test-db/tables/users/rows/resident-1")) {
      return ok({ $id: "resident-1", role: "resident" });
    }
    throw new Error(`Unexpected fetch: ${url}`);
  };

  const result = await invoke({
    path: "/request-status",
    headers: { "x-appwrite-user-id": "resident-1", "x-appwrite-key": "test-key" },
    bodyJson: { requestId: "req-1", status: "Ready" }
  });

  assert.equal(result.response.status, 403);
  assert.equal(result.response.value.ok, false);
  assert.equal(result.response.value.code, "FORBIDDEN");
  assert.match(result.response.value.message, /Staff authorization required/);
  assert.ok(!("error" in result.response.value));
});

test("authenticated resident request is written by the backend", async () => {
  const calls = [];
  globalThis.fetch = async (url, options) => {
    calls.push({ url: String(url), method: options?.method });
    return ok({ $id: "row-1" }, 201);
  };

  const result = await invoke({
    path: "/request",
    headers: { "x-appwrite-user-id": "resident-1", "x-appwrite-key": "test-key" },
    bodyJson: {
      requestId: "request-1",
      serviceId: "clearance",
      referenceNumber: "REF-1",
      details: "purpose|pickup|remarks"
    }
  });

  assert.equal(result.response.status, 200);
  assert.equal(result.response.value.ok, true);
  assert.equal(result.response.value.requestId, "row-1");
  assert.equal(calls.length, 4);
  assert.deepEqual(calls.map(call => call.method), ["POST", "POST", "POST", "POST"]);
});

test("missing users row at a role-gated route returns a generic error", async () => {
  const logged = [];
  const originalConsoleError = console.error;
  console.error = (...args) => { logged.push(args.map(String).join(" ")); };

  globalThis.fetch = async (url) => {
    if (String(url).endsWith("/databases/test-db/tables/users/rows/ghost-1")) {
      return ok({ message: "The requested row was not found.", code: 404 }, 404);
    }
    throw new Error(`Unexpected fetch: ${url}`);
  };

  let result;
  try {
    result = await invoke({
      path: "/request-status",
      headers: { "x-appwrite-user-id": "ghost-1", "x-appwrite-key": "test-key" },
      bodyJson: { requestId: "req-1", status: "Ready" }
    });
  } finally {
    console.error = originalConsoleError;
  }

  assert.equal(result.response.status, 401);
  assert.equal(result.response.value.ok, false);
  assert.equal(result.response.value.code, "UNAUTHENTICATED");
  assert.ok(!("error" in result.response.value));
  assert.doesNotMatch(String(result.response.value.message), /not found|row|appwrite/i);
  assert.ok(
    logged.some(line => /not found/i.test(line)),
    "raw Appwrite detail should be logged server side only"
  );
});

test("invalid status value is rejected", async () => {
  const calls = [];
  globalThis.fetch = async (url, options) => {
    calls.push({ url: String(url), method: options?.method });
    if (String(url).endsWith("/databases/test-db/tables/users/rows/staff-1")) {
      return ok({ $id: "staff-1", role: "staff" });
    }
    if (String(url).endsWith("/databases/test-db/tables/documentRequests/rows/req-1")) {
      return ok({ $id: "req-1", userId: "resident-1", referenceNumber: "REF-1" });
    }
    throw new Error(`Unexpected fetch: ${url}`);
  };

  const result = await invoke({
    path: "/request-status",
    headers: { "x-appwrite-user-id": "staff-1", "x-appwrite-key": "test-key" },
    bodyJson: { requestId: "req-1", status: "Totally Made Up" }
  });

  assert.equal(result.response.status, 400);
  assert.equal(result.response.value.ok, false);
  assert.equal(result.response.value.code, "BAD_REQUEST");
  assert.match(result.response.value.message, /status must be one of/);
  assert.ok(!calls.some(call => call.method === "PATCH"), "must not write an invalid status");
});

test("invalid emergency status value is rejected", async () => {
  globalThis.fetch = async () => {
    throw new Error("fetch should not be called before validation");
  };

  const result = await invoke({
    path: "/emergency-status",
    headers: { "x-appwrite-user-id": "staff-1", "x-appwrite-key": "test-key" },
    bodyJson: { reportId: "rep-1", status: "Done" }
  });

  assert.equal(result.response.status, 400);
  assert.equal(result.response.value.code, "BAD_REQUEST");
});

test("invalid requestId format is rejected", async () => {
  globalThis.fetch = async () => {
    throw new Error("fetch should not be called before validation");
  };

  const result = await invoke({
    path: "/request",
    headers: { "x-appwrite-user-id": "resident-1", "x-appwrite-key": "test-key" },
    bodyJson: {
      requestId: "bad id!!",
      serviceId: "clearance",
      referenceNumber: "REF-1",
      details: "test"
    }
  });

  assert.equal(result.response.status, 400);
  assert.equal(result.response.value.ok, false);
  assert.equal(result.response.value.code, "BAD_REQUEST");
  assert.match(result.response.value.message, /requestId/);
});

test("overlength field is rejected", async () => {
  globalThis.fetch = async () => {
    throw new Error("fetch should not be called before validation");
  };

  const result = await invoke({
    path: "/request",
    headers: { "x-appwrite-user-id": "resident-1", "x-appwrite-key": "test-key" },
    bodyJson: {
      requestId: "request-1",
      serviceId: "clearance",
      referenceNumber: "REF-1",
      details: "x".repeat(2001)
    }
  });

  assert.equal(result.response.status, 400);
  assert.equal(result.response.value.code, "BAD_REQUEST");
  assert.match(result.response.value.message, /details must be at most 2000/);
});

test("history rows are created with owner read permissions", async () => {
  const calls = [];
  globalThis.fetch = async (url, options) => {
    calls.push({
      url: String(url),
      method: options?.method,
      body: options?.body ? JSON.parse(options.body) : null
    });
    return ok({ $id: "row-1" }, 201);
  };

  const create = await invoke({
    path: "/request",
    headers: { "x-appwrite-user-id": "resident-7", "x-appwrite-key": "test-key" },
    bodyJson: {
      requestId: "request-7",
      serviceId: "clearance",
      referenceNumber: "REF-7",
      details: "test"
    }
  });
  assert.equal(create.response.status, 200);

  const historyOnCreate = calls.find(
    call => call.url.endsWith("/tables/requestStatusHistory/rows")
  );
  assert.ok(historyOnCreate, "request history row must be created");
  assert.deepEqual(historyOnCreate.body.permissions, ['read("user:resident-7")']);

  const requestRow = calls.find(
    call => call.url.endsWith("/tables/documentRequests/rows")
  );
  assert.ok(requestRow, "request row must be created");
  assert.deepEqual(requestRow.body.permissions, ['read("user:resident-7")']);

  calls.length = 0;
  globalThis.fetch = async (url, options) => {
    calls.push({
      url: String(url),
      method: options?.method,
      body: options?.body ? JSON.parse(options.body) : null
    });
    if (String(url).endsWith("/tables/users/rows/staff-7")) {
      return ok({ $id: "staff-7", role: "staff" });
    }
    if (String(url).endsWith("/tables/documentRequests/rows/req-7")) {
      return ok({ $id: "req-7", userId: "resident-owner", referenceNumber: "REF-7" });
    }
    return ok({ $id: "row-2" }, 201);
  };

  const update = await invoke({
    path: "/request-status",
    headers: { "x-appwrite-user-id": "staff-7", "x-appwrite-key": "test-key" },
    bodyJson: { requestId: "req-7", status: "Processing", remarks: "checked" }
  });
  assert.equal(update.response.status, 200);

  const historyOnUpdate = calls.find(
    call => call.url.endsWith("/tables/requestStatusHistory/rows")
  );
  assert.ok(historyOnUpdate, "request history row must be created on status update");
  assert.deepEqual(historyOnUpdate.body.permissions, ['read("user:resident-owner")']);

  calls.length = 0;
  const emergency = await invoke({
    path: "/emergency",
    headers: { "x-appwrite-user-id": "resident-8", "x-appwrite-key": "test-key" },
    bodyJson: { reportId: "rep-8", description: "fire" }
  });
  assert.equal(emergency.response.status, 200);
  const emergencyHistory = calls.find(
    call => call.url.endsWith("/tables/emergencyStatusHistory/rows")
  );
  assert.ok(emergencyHistory, "emergency history row must be created");
  assert.deepEqual(emergencyHistory.body.permissions, ['read("user:resident-8")']);
});

test("rate limit triggers once the per-minute budget is exceeded", async () => {
  let fetches = 0;
  globalThis.fetch = async () => {
    fetches += 1;
    return ok({ $id: `em-${fetches}` }, 201);
  };

  const headers = { "x-appwrite-user-id": "rl-user", "x-appwrite-key": "test-key" };
  const results = [];
  for (let i = 0; i < 6; i += 1) {
    const result = await invoke({ path: "/emergency", headers, bodyJson: { description: "fire" } });
    results.push(result.response);
  }

  assert.deepEqual(results.slice(0, 5).map(r => r.status), [200, 200, 200, 200, 200]);
  assert.equal(results[5].status, 429);
  assert.equal(results[5].value.ok, false);
  assert.equal(results[5].value.code, "RATE_LIMITED");
});

test("upstream Appwrite errors are never leaked to callers", async () => {
  globalThis.fetch = async (url) => {
    if (String(url).endsWith("/tables/users/rows/staff-5")) {
      return ok({ $id: "staff-5", role: "staff" });
    }
    if (String(url).endsWith("/tables/documentRequests/rows/req-5")) {
      return ok({ message: "RAW_UPSTREAM_DETAIL internal failure", code: 400 }, 400);
    }
    if (String(url).endsWith("/tables/documentRequests/rows/req-6")) {
      return ok({ message: "RAW_UPSTREAM_500 exploded", code: 500 }, 500);
    }
    throw new Error(`Unexpected fetch: ${url}`);
  };

  const headers = { "x-appwrite-user-id": "staff-5", "x-appwrite-key": "test-key" };

  const rejected = await invoke({
    path: "/request-status",
    headers,
    bodyJson: { requestId: "req-5", status: "Ready" }
  });
  assert.equal(rejected.response.status, 400);
  assert.equal(rejected.response.value.ok, false);
  assert.equal(rejected.response.value.code, "BAD_REQUEST");
  assert.ok(!("error" in rejected.response.value));
  assert.ok(!JSON.stringify(rejected.response.value).includes("RAW_UPSTREAM_DETAIL"));

  const failed = await invoke({
    path: "/request-status",
    headers,
    bodyJson: { requestId: "req-6", status: "Ready" }
  });
  assert.equal(failed.response.value.code, "SERVER_ERROR");
  assert.ok(!JSON.stringify(failed.response.value).includes("RAW_UPSTREAM_500"));
});

test("caller JWT is verified against the user id header when present", async () => {
  const accountCalls = [];
  globalThis.fetch = async (url, options) => {
    if (String(url).endsWith("/account")) {
      accountCalls.push(options?.headers || {});
      return ok({ $id: "someone-else" });
    }
    throw new Error(`Unexpected fetch: ${url}`);
  };

  const rejected = await invoke({
    path: "/request",
    headers: {
      "x-appwrite-user-id": "resident-1",
      "x-appwrite-key": "test-key",
      "x-appwrite-user-jwt": "jwt-token"
    },
    bodyJson: {
      requestId: "request-1",
      serviceId: "clearance",
      referenceNumber: "REF-1",
      details: "test"
    }
  });

  assert.equal(rejected.response.status, 401);
  assert.equal(rejected.response.value.code, "UNAUTHENTICATED");
  assert.equal(accountCalls.length, 1);
  assert.equal(accountCalls[0].Authorization, "Bearer jwt-token");
  assert.equal(accountCalls[0]["X-Appwrite-Key"], undefined);
  assert.equal(accountCalls[0]["x-appwrite-key"], undefined);

  globalThis.fetch = async (url, options) => {
    if (String(url).endsWith("/account")) {
      accountCalls.push(options?.headers || {});
      return ok({ $id: "resident-1" });
    }
    return ok({ $id: "row-1" }, 201);
  };

  const accepted = await invoke({
    path: "/request",
    headers: {
      "x-appwrite-user-id": "resident-1",
      "x-appwrite-key": "test-key",
      "x-appwrite-user-jwt": "jwt-token"
    },
    bodyJson: {
      requestId: "request-1",
      serviceId: "clearance",
      referenceNumber: "REF-1",
      details: "test"
    }
  });

  assert.equal(accepted.response.status, 200);
  assert.equal(accepted.response.value.ok, true);
});
