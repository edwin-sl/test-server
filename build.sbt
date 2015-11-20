name := "TestServer"

version := "1.0"

lazy val `root` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws ,
  "net.debasishg" %% "redisclient" % "3.0",
  "joda-time" % "joda-time" % "2.9.1"
)