ThisBuild / scalaVersion := "3.8.4"
ThisBuild / semanticdbEnabled := true
ThisBuild / organization := "com.jarrahtechnology"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / githubOwner := "jarrahtech"
ThisBuild / githubRepository := "squants"

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

    scalacOptions ++= Seq(
      "-encoding", "utf8", // Option and arguments on same line
      "-Xfatal-warnings",  // New lines for each options
      "-Wunused:all",
      "-deprecation",
      "-feature",
      "-language:noAutoTupling",
      "-Yexplicit-nulls",
      "-Wsafe-init"
    ),

    //https://www.scalatest.org/
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.20" % "test",
    libraryDependencies += "org.scalacheck" %%% "scalacheck" % "1.20.0" % "test"

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
  )

lazy val squantsJS = squants.js
lazy val squantsJVM = squants.jvm
lazy val squantsNative = squants.native
