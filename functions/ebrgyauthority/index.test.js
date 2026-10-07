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
  assert.equal(result.response.status, 400);
  assert.match(result.response.value.error, /Authenticated Appwrite user required/);
});

test("resident cannot change request status", async () => {
  globalThis.fetch = async (url) => {
    if (String(url).endsWith("/databases/test-db/tables/documentRequests/req-1")) {
      return new Response(JSON.stringify({
        $id: "req-1",
        userId: "resident-1",
        referenceNumber: "REF-1"
      }), { status: 200 });
    }
    if (String(url).endsWith("/users/resident-1")) {
      return new Response(JSON.stringify({
        $id: "resident-1",
        role: "resident"
      }), { status: 200 });
    }
    throw new Error(`Unexpected fetch: ${url}`);
  };

  const result = await invoke({
    path: "/request-status",
    headers: { "x-appwrite-user-id": "resident-1", "x-appwrite-key": "test-key" },
    bodyJson: { requestId: "req-1", status: "Ready" }
  });

  assert.equal(result.response.status, 400);
  assert.match(result.response.value.error, /Staff authorization required/);
});

test("authenticated resident request is written by the backend", async () => {
  const calls = [];
  globalThis.fetch = async (url, options) => {
    calls.push({ url: String(url), method: options?.method });
    return new Response(JSON.stringify({ $id: "row-1" }), { status: 201 });
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
