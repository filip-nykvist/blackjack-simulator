val scala3Version = "3.6.3"

lazy val root = project
  .in(file("."))
  .settings(
    name                                   := "BlackJackSimulator",
    organization                           := "com.filipnykvist",
    version                                := "0.1.0",
    scalaVersion                           := scala3Version,
    assembly / assemblyOutputPath          := baseDirectory.value / "BeatTheDealer.jar",
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test
  )
