lazy val root: Project = project.in(file(".")).dependsOn(latestSbtUmbrella)
lazy val latestSbtUmbrella = ProjectRef(uri("git://github.com/kamon-io/kamon-sbt-umbrella.git#kamon-2.x"), "kamon-sbt-umbrella")

//addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.1")
addSbtPlugin("io.crashbox" % "sbt-gpg" % "0.2.0")