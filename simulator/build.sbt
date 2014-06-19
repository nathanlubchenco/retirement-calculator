name := "simulator"

version := "0.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.scalaz" %% "scalaz-core" % "7.0.6"
  //, "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
)


resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)


play.Project.playScalaSettings
