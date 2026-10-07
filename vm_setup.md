# Claude cloud VM setup for this repo

Notes for future Claude cloud sessions on `jarrahtech/squants`. They record what is needed to get `sbt 'clean; testFull'`
running on JVM, JS and Native in a fresh sandbox. Written 2026-10-06 against Scala 3.8.4; the build moved from sbt 1.13.0 to 2.0.10 the same day (see the sbt 2 notes).
Re-check
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

`scripts/claude-cloud-setup.sh` sets up any sbt project, not just this one. It reads the sbt version from
`project/build.properties`, installs sbt, writes `~/.sbt/repositories` with the mirror first (probing it and falling
back to Central if it is unreachable) and puts an `sbt` wrapper on the PATH. Set it as the environment's Setup script (for
example `bash scripts/claude-cloud-setup.sh` from the repo checkout) or run it by hand. Settings are environment variables
described in the script header; `PREWARM=1` also resolves the repo's dependencies. Tested cold: install, wrapper and a
full JVM test run with 0 429s.

Note: Java takes its home directory from the password database, not from `$HOME`, so testing the script with a fake
`HOME` still uses the real caches. Use `-J-Duser.home=...` (and `-Dsbt.global.base`, `-Dsbt.boot.directory`,
`-Dsbt.ivy.home`) for a genuinely cold test.

The mirror matters because the sbt launcher and the build default to `repo.scala-sbt.org` and `repo.typesafe.com`, which
are blocked, while everything this build needs (Scala, the sbt plugins, ScalaTest, ScalaCheck) is on Maven Central.
`local` comes first in the repositories file so that `publishLocal` artifacts resolve for a consumer smoke test. The
launcher still logs download errors for the blocked hosts when it falls through; they are harmless once the mirror copy
is found.

## Run the tests

```sh
cd /home/user/squants
sbt -batch 'clean; testFull'
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
  cap retries at four or five. Downloads that succeed are cached (`~/.cache/coursier`), so each attempt makes progress.
- **The first sbt start on a new sbt version** downloads the launcher dependencies, then Scala 2.12, then the Scala 3,
  Scala.js and Scala Native toolchains.

## Not possible from the sandbox

- Publishing, and anything touching GitHub Packages (`maven.pkg.github.com` is unreachable and `GITHUB_TOKEN` is a
  placeholder). `publishLocal` works, since it stays local.
- Triggering the publish workflow. The user does that.

## sbt 2 notes

The build is on sbt 2.0.10 (since the sbt 2 migration). Things that differ from sbt 1 in this sandbox:

- **`sbt test` can run nothing.** sbt 2's `test` is incremental and cached on disk (`~/.cache/sbt`), so a run over sources
  it has already tested prints `Passed: Total 0 ... No tests to run for Test / testQuick` and still succeeds. Use
  `sbt testFull` for a real run, and always read the test counts before reporting a pass.
- **Thin client and background server.** `sbt` starts a background server (`sbt shutdown` stops it). The server does not
  see command-line `-J` flags, which is why the generated wrapper exports `SBT_OPTS` instead. A running server is reused
  for the same directory, so stop it before changing sbt options or testing from a cold state.
- **Socket path length.** The server's socket path must be short. A deeply nested `HOME` fails with "socket file absolute
  path too long"; set `SBT_GLOBAL_SERVER_DIR=/tmp/s2` (or similar) in that case.
- **Several commands in one call.** The thin client joins separate arguments, so pass one string with semicolons:
  `sbt -batch 'clean; testFull'`, not `sbt -batch clean testFull`.
- **`set` uses Scala names.** Inside `set` use `squantsJVM / publishTo`; on the command line use the project id
  `squants/publish`.
- **Dependencies:** `%%` replaces `%%%`.

Verified cold (fresh install through `scripts/claude-cloud-setup.sh`, fresh Coursier and sbt caches, mirror first):
`testFull` on JVM, JS and Native passed with 0 429s and 0 download errors, about 290 MB downloaded, 1 min 41 s.
