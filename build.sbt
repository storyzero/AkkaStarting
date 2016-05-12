name := "BeginningAkka"

version := "1.0"

scalaVersion := "2.11.8"

lazy val akkaVersion = "2.4.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)

//resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"