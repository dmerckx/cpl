seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

libraryDependencies += "commons-lang" % "commons-lang" % "2.6"

name := "AiryScript"

version := "1.0"

scalaVersion := "2.10.0"

mainClass in Compile := Some("main.Main")

exportJars := true

scalaSource in Compile := file("src/")

unmanagedBase <<= baseDirectory { base => base / "jars" }
