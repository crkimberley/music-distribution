import sbt.*

object Dependencies {

  val zioVersion = "2.0.16"
  val zioJsonVersion = "0.7.3"
  val zioKafkaVersion = "2.0.1"
  val zioHttpVersion = "3.0.1"

  val zioDependencies = Seq(
    "dev.zio" %% "zio" % zioVersion,
    "dev.zio" %% "zio-http" % zioHttpVersion,
    "dev.zio" %% "zio-json" % zioJsonVersion,
    "dev.zio" %% "zio-test" % zioVersion % Test,
    "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
    "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
    "dev.zio" %% "zio-http-testkit" % zioHttpVersion % Test
  )

  val logbackVersion = "1.4.14"

  val loggingDependencies = Seq(
    "ch.qos.logback" % "logback-classic" % logbackVersion % "runtime",
    "ch.qos.logback" % "logback-core" % logbackVersion % "runtime"
  )

  val otherDependencies = Seq(
    "com.github.vickumar1981" % "stringdistance_2.13" % "1.2.7"
  )
}
