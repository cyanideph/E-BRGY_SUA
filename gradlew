#!/usr/bin/env bash
set -euo pipefail

GRADLE_VERSION="9.3.1"
GRADLE_HOME_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/local-gradle/gradle-${GRADLE_VERSION}"
GRADLE_BIN="${GRADLE_HOME_DIR}/bin/gradle"
GRADLE_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
GRADLE_CACHE="${GRADLE_USER_HOME:-$HOME/.gradle}/wrapper/local-gradle/gradle-${GRADLE_VERSION}.zip"

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

if [ ! -x "${GRADLE_BIN}" ]; then
  mkdir -p "$(dirname "${GRADLE_HOME_DIR}")"
  if [ ! -f "${GRADLE_CACHE}" ]; then
    echo "Gradle ${GRADLE_VERSION} not found; downloading bootstrap distribution..." >&2
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL --retry 3 --connect-timeout 15 "${GRADLE_URL}" -o "${GRADLE_CACHE}"
    elif command -v wget >/dev/null 2>&1; then
      wget -q --tries=3 --timeout=15 "${GRADLE_URL}" -O "${GRADLE_CACHE}"
    else
      echo "Neither curl nor wget is available to bootstrap Gradle." >&2
      exit 1
    fi
  fi

  rm -rf "${GRADLE_HOME_DIR}"
  if command -v unzip >/dev/null 2>&1; then
    tmp_dir="$(mktemp -d)"
    trap 'rm -rf "${tmp_dir}"' EXIT
    unzip -q "${GRADLE_CACHE}" -d "${tmp_dir}"
    mv "${tmp_dir}/gradle-${GRADLE_VERSION}" "${GRADLE_HOME_DIR}"
    trap - EXIT
    rm -rf "${tmp_dir}"
  else
    echo "unzip is required to bootstrap Gradle." >&2
    exit 1
  fi
fi

exec "${GRADLE_BIN}" "$@"
