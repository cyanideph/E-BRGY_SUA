#!/usr/bin/env bash
set -euo pipefail
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
echo "Gradle is not installed in this build environment." >&2
exit 1
