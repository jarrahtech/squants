ThisBuild / scalaVersion := "3.8.4"
ThisBuild / semanticdbEnabled := true
ThisBuild / organization := "com.jarrahtechnology"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / version := "1.10.0"

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

lazy val commonSettings = Seq(
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
  libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.20.0" % "test",
)

// ScalaTest's Native build is compiled against test-interface 0.5.10 while the plugin brings 0.5.12.
// sbt 2 turns that strict-scheme eviction into an error; sbt 1 did not.
lazy val nativeCommonSettings = Seq(
  libraryDependencySchemes += "org.scala-native" % "test-interface_native0.5_3" % VersionScheme.Always,
)

lazy val root = project.in(file(".")).
  aggregate(squants.js, squants.jvm, squants.native, squantsIron.js, squantsIron.jvm, squantsIron.native).
  settings(
    publish := {},
    publishLocal := {},
  )

lazy val squants = crossProject(JSPlatform, JVMPlatform, NativePlatform).
  withoutSuffixFor(JVMPlatform).
  in(file(".")).
  settings(
    name := "squants",
    commonSettings,
    publishSettings,
  ).
  jvmSettings(
    libraryDependencies += "org.scala-js" %% "scalajs-stubs" % "1.1.0" % "provided",
    Test / parallelExecution := false,
  ).
  jsSettings(
    Test / parallelExecution := false,
    Test / excludeFilter := "*Serializer.scala" || "*SerializerSpec.scala",
  ).
  nativeSettings(nativeCommonSettings)

// Iron refinement support for quantities. A separate module so that core stays free of runtime dependencies.
lazy val squantsIron = crossProject(JSPlatform, JVMPlatform, NativePlatform).
  withoutSuffixFor(JVMPlatform).
  in(file("iron")).
  dependsOn(squants).
  settings(
    name := "squants-iron",
    commonSettings,
    publishSettings,
    libraryDependencies += "io.github.iltotore" %% "iron" % "3.3.2",
    Test / parallelExecution := false,
  ).
  nativeSettings(nativeCommonSettings)

lazy val squantsJS = squants.js
lazy val squantsJVM = squants.jvm
lazy val squantsNative = squants.native
lazy val squantsIronJS = squantsIron.js
lazy val squantsIronJVM = squantsIron.jvm
lazy val squantsIronNative = squantsIron.native
