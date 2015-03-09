name := "simulator"

version := "0.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.scalaz" %% "scalaz-core" % "7.0.6",
  "com.pellucid" %% "framian" % "0.1.1"
  //, "org.scalacheck" %% "scalacheck" % "1.11.4" % "test"
)


resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Pellucid Bintray" at "http://dl.bintray.com/pellucid/maven"
)


play.Project.playScalaSettings
