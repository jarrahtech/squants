# Claude cloud VM setup for this repo

Notes for future Claude cloud sessions on `jarrahtech/squants`. They record what was needed to get `sbt 'clean; test'`
running on JVM, JS and Native in a fresh sandbox, and what went wrong on the way. Written 2026-10-06 against Scala 3.7.1
and sbt 1.11.2; re-check versions before relying on them.

## What the sandbox has and lacks

| Item | State in a fresh sandbox |
|---|---|
| JDK | OpenJDK 21 installed (enough for Scala 3.8) |
| Node.js | 22.x installed (Scala.js tests) |
| clang | 18.x installed (Scala Native tests) |
| sbt | **Not installed** |
| `repo1.maven.org` (Maven Central) | Reachable, but rate-limited (HTTP 429) |
| `repo.scala-sbt.org`, `repo.typesafe.com` | **Blocked** by the egress policy (HTTP 403 on CONNECT) |
| `maven.pkg.github.com` | Unreachable |
| `GITHUB_TOKEN` | Set to the placeholder `proxy-injected`; enough for the build to load, useless for GitHub Packages |

Do not try to route around the blocked hosts or disable TLS verification. If a build needs a blocked host, report which
one. The proxy status endpoint names recent denials:
`curl -sS "$HTTPS_PROXY/__agentproxy/status"`.

## Setup

Use the session scratchpad directory (shown in the system prompt) for everything below, so the repo stays clean.
`S` is that directory.

### 1. Install sbt

Match the version in `project/build.properties`.

```sh
S=<scratchpad directory>
mkdir -p $S/sbtdl $S/sbt-home
curl -sSL -o $S/sbtdl/sbt.tgz https://github.com/sbt/sbt/releases/download/v1.11.2/sbt-1.11.2.tgz
tar -xzf $S/sbtdl/sbt.tgz -C $S/sbt-home
```

The release download from `github.com` works through the proxy.

### 2. Point sbt at Maven Central only

The launcher and build default to `repo.scala-sbt.org` and `repo.typesafe.com`, which are blocked. Everything this build
needs (Scala, the sbt plugins, ScalaTest, ScalaCheck) is on Maven Central, so use a repositories file:

```sh
cat > $S/repositories <<'EOF'
[repositories]
  local
  maven-central: https://repo1.maven.org/maven2/
EOF
```

### 3. Wrapper script

```sh
cat > $S/sbtw.sh <<EOF
#!/bin/sh
exec $S/sbt-home/sbt/bin/sbt \\
  -J-Dsbt.override.build.repos=true \\
  -J-Dsbt.repository.config=$S/repositories \\
  -Dsbt.boot.directory=$S/boot \\
  -Dsbt.ivy.home=$S/ivy \\
  -J-Dcoursier.parallel-downloads=2 "\$@"
EOF
chmod +x $S/sbtw.sh
```

The launcher will still log download errors for the blocked hosts. They are harmless once the Maven Central copy is
found.

### 4. Run the tests

```sh
export COURSIER_PARALLEL_DOWNLOADS=2
cd /home/user/squants
$S/sbtw.sh -batch 'clean; test'
```

Run it in the background and read the log, because a full run takes several minutes.

## Known problems

- **Maven Central 429s.** The sandbox shares an egress address and Central rate-limits bursts. Symptoms are
  `Server returned HTTP response code: 429` and `Error downloading ...` / `Missing ...` from `update`. The two
  parallelism settings above (`-J-Dcoursier.parallel-downloads=2` and `COURSIER_PARALLEL_DOWNLOADS=2`) helped far more
  than retrying. Fast, repeated retries made it worse; the count of 429s went from 14 to 181 over eight tight retries.
  Wait a minute between attempts. Downloads that succeed are cached (`~/.cache/coursier` and `$S/ivy`), so each attempt
  makes progress. Cap retries at four or five.
- **A failed platform does not stop the others.** One `update` failure can leave JVM and Native tested but JS not run.
  Re-run only the missing one, for example `squantsJS/clean; squantsJS/test`. Never report an unrun platform as
  passing.
- **Check the real sbt exit code.** A backgrounded `sbt ... > log; echo "exit $?"` reports the shell's exit status. Read
  the last lines of the log (`[success]` or `[error]`) before saying anything passed.
- **The first sbt start is slow.** The launcher downloads sbt itself and Scala 2.12.20, then the build downloads
  Scala 3, Scala.js and Scala Native toolchains.

## Not possible from the sandbox

- Publishing, and anything touching GitHub Packages (`maven.pkg.github.com` is unreachable and `GITHUB_TOKEN` is a
  placeholder). `publishLocal` should work, since it stays local.
- Triggering the publish workflow. The user does that.

## Suggestions to the user for the environment

- Add `repo.scala-sbt.org` and `repo.typesafe.com` to the environment's network allowlist, which removes the need for the
  repositories override.
- Add a setup script that does steps 1 to 3, so a new session starts with sbt ready. Use the `read_documentation` tool
  (topics `environment.setup_script` and `environment.network`) for how.

## Baseline (untouched checkout, Scala 3.7.1, sbt 1.11.2, JDK 21)

| Platform | ScalaTest | ScalaCheck |
|---|---|---|
| JVM | 1008 passed, 0 failed, 1 pending (89 suites) | 65 passed |
| Native | 1008 passed, 0 failed, 1 pending (89 suites) | 65 passed |
| JS | 1008 passed, 0 failed, 1 pending (89 suites) | 65 passed |
