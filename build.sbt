name := "NettySyncClient"

version := "1.0"

scalaVersion := "2.11.8"

val commonVersion = "0.2.21-SNAPSHOT"
val projectScalaVersion = "2.11.7"
val specs2Version = "2.5"

val specs2Dependency = "org.specs2" %% "specs2-core" % specs2Version % "test"
val specs2JunitDependency = "org.specs2" %% "specs2-junit" % specs2Version % "test"
val specs2MockDependency = "org.specs2" %% "specs2-mock" % specs2Version % "test"
val logbackDependency = "ch.qos.logback" % "logback-classic" % "1.1.6" % "test"

val commonDependencies = Seq(
  "org.slf4j" % "slf4j-api" % "1.7.18",
  "joda-time" % "joda-time" % "2.9.2",
  "org.joda" % "joda-convert" % "1.8.1",
  "io.netty" % "netty-all" % "4.1.1.Final",
  "org.javassist" % "javassist" % "3.20.0-GA",
  specs2Dependency,
  specs2JunitDependency,
  specs2MockDependency,
  logbackDependency
)

libraryDependencies ++= commonDependencies