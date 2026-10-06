# Claude cloud VM setup for this repo

Notes for future Claude cloud sessions on `jarrahtech/squants`. They record what is needed to get `sbt 'clean; test'`
running on JVM, JS and Native in a fresh sandbox. Written 2026-10-06 against Scala 3.8.4 and sbt 1.13.0; re-check
versions before relying on them.

## What the sandbox has and lacks

| Item | State in a fresh sandbox |
|---|---|
| JDK | OpenJDK 21 installed (Scala 3.8 needs Java 17 or later) |
| Node.js | 22.x installed (Scala.js tests) |
| clang | 18.x installed (Scala Native tests) |
| sbt | **Not installed** |
| `repo1.maven.org` and `repo.maven.apache.org` (Maven Central) | Reachable, but **heavily rate-limited**: about 40% of requests in a parallel burst return HTTP 429 |
| `maven-central.storage-download.googleapis.com` (Google's Maven Central mirror) | Reachable, and **not rate-limited** (60 of 60 burst requests returned 200) |
| `repo.scala-sbt.org`, `repo.typesafe.com`, `s01.oss.sonatype.org`, `central.sonatype.com` | **Blocked** by the egress policy (HTTP 403 on CONNECT) |
| `maven.pkg.github.com` | Unreachable |
| `GITHUB_TOKEN` | Set to the placeholder `proxy-injected`; enough for the build to load, useless for GitHub Packages |

Do not try to route around blocked hosts or disable TLS verification. If a build needs a blocked host, report which one.
The proxy status endpoint names recent denials: `curl -sS "$HTTPS_PROXY/__agentproxy/status"`.

## Quick setup: use the script

`scripts/claude-cloud-setup.sh` does steps 1 to 3 below for any sbt project, not just this one. It reads the sbt version
from `project/build.properties`, installs sbt, writes `~/.sbt/repositories` with the mirror first (probing it and falling
back to Central if it is unreachable) and puts an `sbt` wrapper on the PATH. Set it as the environment's Setup script (for
example `bash scripts/claude-cloud-setup.sh` from the repo checkout) or run it by hand. Settings are environment variables
described in the script header; `PREWARM=1` also resolves the repo's dependencies. Tested cold: install, wrapper and a
full JVM test run with 0 429s. The manual steps below are the same thing spelled out.

Note: Java takes its home directory from the password database, not from `$HOME`, so testing the script with a fake
`HOME` still uses the real caches. Use `-J-Duser.home=...` (and `-Dsbt.global.base`, `-Dsbt.boot.directory`,
`-Dsbt.ivy.home`) for a genuinely cold test.

## Setup

Use the session scratchpad directory (shown in the system prompt) for everything below, so the repo stays clean.
`S` is that directory.

### 1. Install sbt

Match the version in `project/build.properties`. The release download from `github.com` works through the proxy.

```sh
S=<scratchpad directory>
mkdir -p $S/sbtdl $S/sbt-home
curl -sSL -o $S/sbtdl/sbt.tgz https://github.com/sbt/sbt/releases/download/v1.13.0/sbt-1.13.0.tgz
tar -xzf $S/sbtdl/sbt.tgz -C $S/sbt-home
```

### 2. Resolve from the Google Maven Central mirror first

The sbt launcher and the build default to `repo.scala-sbt.org` and `repo.typesafe.com`, which are blocked. Everything this
build needs (Scala, the sbt plugins, ScalaTest, ScalaCheck) is on Maven Central. Put the Google mirror first and Central
second as a fallback:

```sh
cat > $S/repositories <<'EOF'
[repositories]
  local
  gcs-mirror: https://maven-central.storage-download.googleapis.com/maven2/
  maven-central: https://repo1.maven.org/maven2/
EOF
```

Put `local` first so `publishLocal` artifacts resolve for a consumer smoke test.

### 3. Wrapper script

```sh
cat > $S/sbtw.sh <<EOF
#!/bin/sh
exec $S/sbt-home/sbt/bin/sbt \\
  -J-Dsbt.override.build.repos=true \\
  -J-Dsbt.repository.config=$S/repositories \\
  -Dsbt.boot.directory=$S/boot \\
  -Dsbt.ivy.home=$S/ivy "\$@"
EOF
chmod +x $S/sbtw.sh
```

The launcher still logs download errors for the blocked hosts when it falls through; they are harmless once the mirror
copy is found.

### 4. Run the tests

```sh
cd /home/user/squants
$S/sbtw.sh -batch 'clean; test'
```

Run it in the background and read the log, because a full cold run takes a couple of minutes.

## sbt project ids

The JVM project id is `squants` (the JVM suffix is dropped), not `squantsJVM`. The others are `squantsJS` and
`squantsNative`. For one platform: `squants/test`, `squantsJS/test`, `squantsNative/test`. `squantsJVM` is only the Scala
val name, so `sbt squantsJVM/test` fails with "Not a valid command".

## Evidence that the mirror fixes the failures

- **Burst test:** 60 small POM requests, 8 in parallel. Maven Central: 36 returned 200 and 24 returned 429.
  `repo.maven.apache.org`: 37 and 23. Google mirror: 60 returned 200.
- **Cold run through the mirror:** fresh Coursier cache, boot and Ivy directories, one attempt, no retry loop.
  `clean; test` on all three platforms succeeded with **0** 429s and **0** download errors (about 235 MB, 2 min 9 s).
- **Before the mirror (Central only):** eight tight retries made things worse (14 to 181 429s), and it took many slow
  attempts across several runs to get a baseline.

Earlier versions of these notes recommended lowering Coursier's download parallelism. That used a property that does not
exist (`coursier.parallel-downloads`) and was never shown to help; do not use it. Coursier's real settings, found in the
lm-coursier jar, are `coursier.parallel-download-count`, `coursier.exception-retry`,
`coursier.exception-retry-backoff-initial-delay` and `coursier.exception-retry-backoff-multiplier`. They were not tested
here and should not be needed with the mirror.

## If it still fails

- **Check the real sbt exit code.** A backgrounded `sbt ... > log; echo "exit $?"` reports the shell's exit status. Read
  the last lines of the log (`[success]` or `[error]`) before saying anything passed.
- **A failed platform does not stop the others.** One `update` failure can leave JVM and Native tested but JS not run.
  Re-run only the missing one, for example `squantsJS/clean; squantsJS/test`. Never report an unrun platform as passing.
- **429s from Central.** If the mirror itself stops working, fall back to Central only, wait a minute between attempts and
  cap retries at four or five. Downloads that succeed are cached (`~/.cache/coursier` and `$S/ivy`), so each attempt
  makes progress.
- **The first sbt start on a new sbt version** downloads the launcher dependencies, then Scala 2.12, then the Scala 3,
  Scala.js and Scala Native toolchains.

## Not possible from the sandbox

- Publishing, and anything touching GitHub Packages (`maven.pkg.github.com` is unreachable and `GITHUB_TOKEN` is a
  placeholder). `publishLocal` works, since it stays local.
- Triggering the publish workflow. The user does that.

## Suggestions to the user for the environment

- Add a setup script that does steps 1 to 3, so a new session starts with sbt ready and the mirror configured.
  (Environment settings, then Setup script.)
- Adding `repo.scala-sbt.org` and `repo.typesafe.com` to the network allowlist is no longer necessary, since the mirror
  covers everything this build needs.

## Observed on Scala 3.8.4 / sbt 1.13.0 (Task 1)

JVM, JS and Native all pass 1008 ScalaTest tests and 65 ScalaCheck properties. `publishLocal` for all three platforms and
a throwaway Scala 3.8.4 consumer project (run on JVM, JS and Native) also worked. For the consumer project, JS needs
`scalaJSUseMainModuleInitializer := true` to run a `main`.

## Baseline (untouched checkout, Scala 3.7.1, sbt 1.11.2, JDK 21)

| Platform | ScalaTest | ScalaCheck |
|---|---|---|
| JVM | 1008 passed, 0 failed, 1 pending (89 suites) | 65 passed |
| Native | 1008 passed, 0 failed, 1 pending (89 suites) | 65 passed |
| JS | 1008 passed, 0 failed, 1 pending (89 suites) | 65 passed |
