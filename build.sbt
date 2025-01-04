import Dependencies.{loggingDependencies, otherDependencies, zioDependencies}

name := "music-distribution"
organization := "com.chriskimberley"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.6.2"

lazy val compilerOptions = Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Wunused:all",
  "-Wshadow:all",
  "-explaintypes"
)

scalacOptions ++= compilerOptions

lazy val coreDependencies = zioDependencies ++ loggingDependencies ++ otherDependencies
libraryDependencies ++= coreDependencies

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
