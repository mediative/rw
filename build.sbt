scalaVersion := "2.11.5"

// for scalaz-stream
resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "2.4.15" % "test",          // http://etorreborre.github.io/specs2/
  "org.specs2" %% "specs2-scalacheck" % "2.4.15" % "test",    // ^
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",  // https://github.com/typesafehub/scala-logging
  "org.scalaz.stream" %% "scalaz-stream" % "0.6a",            // https://github.com/scalaz/scalaz-stream
  "org.scalaz" %% "scalaz-core" % "7.1.1",                    // https://github.com/scalaz/scalaz
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.1.1"       // ^
  

  // "com.typesafe.slick" %% "slick" % "2.1.0",                  // http://slick.typesafe.com/doc/2.1.0/
  // "com.github.fommil" %% "freeslick" % "2.0.3-SNAPSHOT"       // https://github.com/fommil/freeslick
)

// for specs2 macros
scalacOptions in Test += "-Yrangepos"

// bug with specs2 https://github.com/non/kind-projector/issues/7
// https://github.com/non/kind-projector
// resolvers += "bintray/non" at "http://dl.bintray.com/non/maven"
// addCompilerPlugin("org.spire-math" % "kind-projector_2.11" % "0.5.2")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Yrangepos"
  // "-Xlog-implicits"
)

import scalariform.formatter.preferences._

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignParameters, true)