ThisBuild / scalaVersion := "3.8.4"
ThisBuild / semanticdbEnabled := true
ThisBuild / organization := "com.jarrahtechnology"
ThisBuild / versionScheme := Some("early-semver")

// Publishing to GitHub Packages. The token comes from the GITHUB_TOKEN environment variable (as CI sets it).
val githubRepoUrl = "https://github.com/jarrahtech/squants"
val githubPublishRepo = "https://maven.pkg.github.com/jarrahtech/squants"
lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := Some("GitHub Packages" at githubPublishRepo),
  credentials += Credentials("GitHub Package Registry", "maven.pkg.github.com", "jarrahtech", sys.env.getOrElse("GITHUB_TOKEN", "")),
  // Project metadata that published POMs carry (sbt-github-packages used to supply it).
  homepage := Some(url(githubRepoUrl)),
  organizationHomepage := Some(url(githubRepoUrl)),
  scmInfo := Some(ScmInfo(url(githubRepoUrl), "scm:git@github.com:jarrahtech/squants.git")),
  // Do not copy this build's resolvers into published POMs.
  pomIncludeRepository := (_ => false),
)

lazy val root = project.in(file(".")).
  aggregate(squants.js, squants.jvm, squants.native).
  settings(
    publish := {},
    publishLocal := {},
  )

lazy val squants = crossProject(JSPlatform, JVMPlatform, NativePlatform).
  withoutSuffixFor(JVMPlatform).
  in(file(".")).
  settings(
    name := "squants",
    version := "1.9.0",
    publishSettings,

    scalacOptions ++= Seq(
      "-encoding", "utf8", // Option and arguments on same line
      "-Werror",            // New lines for each options
      "-Wunused:all",
      "-deprecation",
      "-feature",
      "-language:noAutoTupling",
      "-Yexplicit-nulls",
      "-Wsafe-init"
    ),

    //https://www.scalatest.org/
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % "test",
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.20.0" % "test"

  ).
  jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.1.0" % "provided",
    Test / parallelExecution := false,
  ).
  jsSettings(
    Test / parallelExecution := false,
    Test / excludeFilter := "*Serializer.scala" || "*SerializerSpec.scala",
  ).
  nativeSettings(
    // ScalaTest's Native build is compiled against test-interface 0.5.10 while the plugin brings 0.5.12.
    // sbt 2 turns that strict-scheme eviction into an error; sbt 1 did not.
    libraryDependencySchemes += "org.scala-native" % "test-interface_native0.5_3" % VersionScheme.Always,
  )

lazy val squantsJS = squants.js
lazy val squantsJVM = squants.jvm
lazy val squantsNative = squants.native
