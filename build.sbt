name := "bildungsroman"

version := "1.0"

scalaVersion := "2.9.2"

scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++=  Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.1",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)
