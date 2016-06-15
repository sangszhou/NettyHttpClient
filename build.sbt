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
val scalatest = "org.scalatest" % "scalatest_2.11" % "3.0.0-M2"

val commonDependencies = Seq(
  "org.slf4j" % "slf4j-api" % "1.7.18",
  "joda-time" % "joda-time" % "2.9.2",
  "org.joda" % "joda-convert" % "1.8.1",
  "io.netty" % "netty-all" % "4.1.1.Final",
  "org.javassist" % "javassist" % "3.20.0-GA",
  "com.typesafe" % "config" % "1.3.0",
  "org.apache.httpcomponents"        % "httpclient"                 %   "4.5.2",
  "org.springframework"              % "spring-web"                 % "4.2.6.RELEASE",
  specs2Dependency,
  specs2JunitDependency,
  specs2MockDependency,
  logbackDependency,
  scalatest
)

libraryDependencies ++= commonDependencies