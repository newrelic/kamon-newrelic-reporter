organization := "com.newrelic.telemetry"
name := "kamon-newrelic-reporter"
version := "0.0.4-SNAPSHOT"
description := "New Relic Kamon Reporter"

scalaVersion := "2.13.0"

resolvers += Resolver.mavenLocal
resolvers += Resolver.bintrayRepo("kamon-io", "snapshots")

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

credentials += Credentials(new File("credentials.properties"))
credentials += Credentials(
  "GnuPG Key ID",
  "gpg",
  "395D1F263AFE4F3C06BAAB646922760724F67BF8", // key identifier
  "ignored" // this field is ignored; passwords are supplied by pinentry
)

libraryDependencies ++= Seq(
  "io.kamon" %% "kamon-core" % "2.0.1",
  "com.newrelic.telemetry" % "telemetry" % "0.3.4",
  "com.newrelic.telemetry" % "telemetry-http-okhttp" % "0.3.4",
  scalatest % "test",
  "org.mockito" % "mockito-core" % "3.1.0" % "test"
)
