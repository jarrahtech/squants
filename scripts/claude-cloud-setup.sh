#!/usr/bin/env bash
# Sets up sbt in a Claude cloud sandbox so Scala builds work from the first command.
#
# What it does (idempotent, safe to re-run):
#   1. Installs sbt from the GitHub release tarball. The version comes from SBT_VERSION, else from
#      project/build.properties in the repo, else a default.
#   2. Writes ~/.sbt/repositories so dependency lookups try a Maven Central mirror first and Maven Central second.
#      The default sbt repositories (repo.scala-sbt.org, repo.typesafe.com) are blocked in the sandbox, and Maven Central
#      rate-limits it (HTTP 429), which makes cold builds fail. The mirror is only used if a probe request succeeds.
#   3. Puts an `sbt` wrapper on the PATH that points sbt at that file and ignores resolvers declared in builds.
#   4. Optionally (PREWARM=1) resolves the repo's dependencies now so later sessions start warm.
#
# Use it as the environment's Setup script (run from the repo checkout), or run it by hand.
# Needs: bash, curl, tar, a JDK. It does not install a JDK, Node.js or clang; it only reports whether they are present.
#
# Settings (environment variables, all optional):
#   SBT_VERSION      sbt version to install              (default: from project/build.properties, else 1.13.0)
#   REPO_DIR         repo to read build.properties from  (default: current directory, else first /home/*/* repo)
#   SBT_PREFIX       where sbt is unpacked               (default: /opt/sbt if writable, else ~/.local/sbt)
#   BIN_DIR          where the wrapper goes              (default: /usr/local/bin if writable, else ~/.local/bin)
#   MAVEN_MIRROR     mirror base URL, or "off" to skip   (default: Google's Maven Central mirror)
#   OVERRIDE_BUILD_REPOS  "true" ignores resolvers declared in builds; "false" keeps them (default: true)
#   PREWARM          1 to run `sbt update` in REPO_DIR   (default: 0)
set -euo pipefail

DEFAULT_SBT_VERSION="1.13.0"
MAVEN_MIRROR="${MAVEN_MIRROR:-https://maven-central.storage-download.googleapis.com/maven2}"
CENTRAL="https://repo1.maven.org/maven2"
OVERRIDE_BUILD_REPOS="${OVERRIDE_BUILD_REPOS:-true}"
PREWARM="${PREWARM:-0}"

log() { printf '[claude-cloud-setup] %s\n' "$*"; }
warn() { printf '[claude-cloud-setup] WARNING: %s\n' "$*" >&2; }

# --- locate the repo and the sbt version -----------------------------------------------------------------------------
find_repo() {
  if [ -n "${REPO_DIR:-}" ]; then echo "$REPO_DIR"; return; fi
  if [ -f project/build.properties ]; then pwd; return; fi
  local d
  for d in /home/*/*/; do
    [ -f "${d}project/build.properties" ] && { echo "${d%/}"; return; }
  done
  echo ""
}
REPO="$(find_repo)"

if [ -z "${SBT_VERSION:-}" ]; then
  if [ -n "$REPO" ] && [ -f "$REPO/project/build.properties" ]; then
    SBT_VERSION="$(sed -n 's/^sbt\.version *= *//p' "$REPO/project/build.properties" | tr -d '[:space:]')"
  fi
  SBT_VERSION="${SBT_VERSION:-$DEFAULT_SBT_VERSION}"
fi
log "sbt version: $SBT_VERSION (repo: ${REPO:-none found})"

# --- choose install locations ----------------------------------------------------------------------------------------
writable_dir() { mkdir -p "$1" 2>/dev/null && [ -w "$1" ]; }
if [ -n "${SBT_PREFIX:-}" ]; then :; elif writable_dir /opt/sbt; then SBT_PREFIX=/opt/sbt; else SBT_PREFIX="$HOME/.local/sbt"; fi
if [ -n "${BIN_DIR:-}" ]; then :; elif writable_dir /usr/local/bin; then BIN_DIR=/usr/local/bin; else BIN_DIR="$HOME/.local/bin"; fi
mkdir -p "$SBT_PREFIX" "$BIN_DIR" "$HOME/.sbt"

# --- 1. install sbt --------------------------------------------------------------------------------------------------
SBT_HOME_DIR="$SBT_PREFIX/sbt-$SBT_VERSION"
REAL_SBT="$SBT_HOME_DIR/bin/sbt"
if [ -x "$REAL_SBT" ]; then
  log "sbt $SBT_VERSION already installed at $SBT_HOME_DIR"
else
  tmp="$(mktemp -d)"
  trap 'rm -rf "$tmp"' EXIT
  url="https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.tgz"
  log "downloading $url"
  curl -fsSL --retry 5 --retry-delay 3 --retry-all-errors -o "$tmp/sbt.tgz" "$url"
  tar -xzf "$tmp/sbt.tgz" -C "$tmp"          # unpacks to $tmp/sbt
  rm -rf "$SBT_HOME_DIR"
  mv "$tmp/sbt" "$SBT_HOME_DIR"
  log "installed sbt $SBT_VERSION at $SBT_HOME_DIR"
fi

# --- 2. repositories file --------------------------------------------------------------------------------------------
REPOS_FILE="$HOME/.sbt/repositories"
use_mirror=0
if [ "$MAVEN_MIRROR" != "off" ]; then
  probe="$MAVEN_MIRROR/org/scala-lang/scala-library/2.13.16/scala-library-2.13.16.pom"
  if curl -fsS -o /dev/null --max-time 20 --retry 2 --retry-all-errors "$probe" 2>/dev/null; then
    use_mirror=1
  else
    warn "mirror $MAVEN_MIRROR is not reachable; using Maven Central only"
  fi
fi
{
  echo "[repositories]"
  echo "  local"
  [ "$use_mirror" = 1 ] && echo "  mirror: $MAVEN_MIRROR/"
  echo "  maven-central: $CENTRAL/"
} > "$REPOS_FILE"
log "wrote $REPOS_FILE (mirror first: $([ "$use_mirror" = 1 ] && echo yes || echo no))"

# --- 3. wrapper ------------------------------------------------------------------------------------------------------
WRAPPER="$BIN_DIR/sbt"
cat > "$WRAPPER" <<EOF
#!/bin/sh
# Generated by claude-cloud-setup.sh: runs sbt $SBT_VERSION against $REPOS_FILE.
# The options go in SBT_OPTS (not on the command line) so that the background server started by sbt 2's thin client
# inherits them too.
SBT_OPTS="-Dsbt.override.build.repos=$OVERRIDE_BUILD_REPOS -Dsbt.repository.config=$REPOS_FILE\${SBT_OPTS:+ \$SBT_OPTS}"
export SBT_OPTS
exec "$REAL_SBT" "\$@"
EOF
chmod +x "$WRAPPER"
log "wrote wrapper $WRAPPER"
case ":$PATH:" in *":$BIN_DIR:"*) ;; *) warn "$BIN_DIR is not on PATH; add it or call $WRAPPER directly" ;; esac

# --- toolchain report ------------------------------------------------------------------------------------------------
for tool in java node clang; do
  if command -v "$tool" >/dev/null 2>&1; then
    case "$tool" in
      java) v="$(java -version 2>&1 | grep -v 'Picked up' | head -1)" ;;
      *) v="$("$tool" --version 2>&1 | head -1)" ;;
    esac
    log "$tool: $v"
  else
    warn "$tool not found (needed for: java=any build, node=Scala.js tests, clang=Scala Native tests)"
  fi
done

# --- 4. optional prewarm ---------------------------------------------------------------------------------------------
if [ "$PREWARM" = 1 ]; then
  if [ -n "$REPO" ]; then
    log "prewarming dependencies in $REPO (sbt update; Test/update)"
    ( cd "$REPO" && "$WRAPPER" -batch 'update; Test/update' ) || warn "prewarm failed; the first build will download instead"
  else
    warn "PREWARM=1 but no repo found; skipping"
  fi
fi
log "done"
