libraryDependencies ++= Seq(
    "org.specs2" %% "specs2" % "1.10" % "test"
)
  
resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                    "releases"  at "http://oss.sonatype.org/content/repositories/releases")