organization := "com.newrelic.telemetry"
name := "kamon-newrelic-reporter"
version := "0.0.1-SNAPSHOT"
description := "New Relic Kamon Reporter"

scalaVersion := "2.13.0"

resolvers += Resolver.mavenLocal
resolvers += Resolver.bintrayRepo("kamon-io", "snapshots")

// these are just for republishing local non-SNAPSHOT builds
//publishConfiguration := publishConfiguration.value.withOverwrite(true)
//publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
//publishM2Configuration := publishM2Configuration.value.withOverwrite(true)
//publishTo := Some("Sonatype Snapshots Nexus" at "https://oss.sonatype.org/content/repositories/snapshots")


publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(new File("credentials.properties"))

libraryDependencies ++= Seq(
  "io.kamon" %% "kamon-core" % "2.0.1",
  "io.zipkin.reporter2" % "zipkin-reporter" % "2.10.3",
  "io.zipkin.reporter2" % "zipkin-sender-okhttp3" % "2.10.3",
  "com.newrelic.telemetry" % "telemetry" % "0.3.2",
  "com.newrelic.telemetry" % "telemetry-http-okhttp" % "0.3.2",
  scalatest % "test",
  "org.mockito" % "mockito-core" % "3.1.0"
)
