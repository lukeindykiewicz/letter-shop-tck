name := "letter-shop-tck"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaVer = "2.4.7"
  Seq(
    "org.specs2" %% "specs2-core" % "3.8.3" % "test" withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-actor" % akkaVer % "test" withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-http-core" % akkaVer % "test" withSources() withJavadoc(),
    "com.typesafe.akka" %% "akka-stream" % akkaVer % "test" withSources() withJavadoc(),
    "com.typesafe" % "config" % "1.3.0" % "test" withSources() withJavadoc(),
    "com.lihaoyi" %% "upickle" % "0.4.1" % "test" withSources() withJavadoc()
  )
}
