#!/usr/bin/env bash
set -euo pipefail

# Ramus PR previews use a clean build environment. Keep the project runnable
# without requiring a checked-in binary Gradle wrapper JAR.
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Gradle is not installed in this build environment." >&2
exit 1
