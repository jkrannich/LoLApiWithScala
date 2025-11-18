ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

ThisBuild / resolvers += Resolver.mavenLocal

lazy val root = (project in file("."))
  .settings(
    name := "LoLApiWIthScala"
  )

libraryDependencies += "io.github.jkrannich" % "fiddlesticks" % "1.0-SNAPSHOT"
libraryDependencies += "io.github.cdimascio" % "dotenv-java" % "3.2.0"
