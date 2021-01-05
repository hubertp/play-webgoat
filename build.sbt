name := "play-webgoat"

version := "1.0"

lazy val `play-webgoat` = (project in file(".")).enablePlugins(PlayScala)

crossScalaVersions := Seq("2.12.10", "2.11.12", "2.13.1")
scalaVersion := crossScalaVersions.value.head // tc-skip
scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation",
  "-Xfatal-warnings")

scalacOptions ++=
  (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n >= 12 =>
      Seq("-Xlint:-unused") // "unused" is too fragile w/ Twirl, routes file
    case _ =>
      Seq("-Xlint")
  })

//libraryDependencies += guice
libraryDependencies += ws

// Big standalone jar configuration (not officially supported by Play)

mainClass in assembly := Some("play.core.server.ProdServerStart")
fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

assemblyMergeStrategy in assembly := {
  case PathList("javax", "persistence", xs @ _*)     => MergeStrategy.last
  case PathList("javax", "transaction", xs @ _*)     => MergeStrategy.last
  case PathList("org", "apache", "commons", "logging", xs @ _*)     => MergeStrategy.last
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".sql"  => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case "play/reference-overrides.conf"               => MergeStrategy.first
  case x if x.startsWith("javax") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.withClassifier(Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)
