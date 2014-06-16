name := "simulator"

version := "0.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.scalaz" %% "scalaz-core" % "7.0.6"
)

play.Project.playScalaSettings
