import sbtcrossproject.CrossPlugin.autoImport.crossProject

val commonSettings = Seq(
  version := "1.5.2",
  scalaVersion := "2.13.0",
  crossScalaVersions := Seq("2.12.9", "2.13.0"),
  scalacOptions ++= Seq("-feature", "-deprecation", "-Xlint"),
  scalacOptions in (Compile, doc) += "-no-link-warnings"
) ++ metadata ++ publishing

lazy val metadata = Seq(
  organization := "com.kyleu",
  homepage := Some(url("https://stanch.github.io/zipper/")),
  scmInfo := Some(ScmInfo(url("https://github.com/kyleu/zipper"), "scm:git@github.com:kyleu/zipper.git")),
  developers := List(
    Developer(id = "stanch", name = "Nick Stanchenko", email = "nick.stanch@gmail.com", url = url("https://github.com/stanch")),
    Developer(id = "kyleu", name = "Kyle Unverferth", email = "opensource@kyleu.com", url = url("http://kyleu.com"))
  ),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
)

lazy val publishing = Seq(
  publishMavenStyle := true,
  publishTo := Some(MavenRepository("sonatype-staging", "https://oss.sonatype.org/service/local/staging/deploy/maven2"))
)

lazy val zipper = crossProject(JSPlatform, JVMPlatform).in(file("."))
  .settings(commonSettings)
  .settings(
    name := "zipper",
    version := "0.5.2",
    libraryDependencies ++= Seq(
      "com.chuusai" %%% "shapeless" % "2.3.3",
      "org.scalatest" %%% "scalatest" % "3.0.8" % Test
    )
  )

lazy val zipperJVM = zipper.jvm
lazy val zipperJS = zipper.js

lazy val root = project.in(file("."))
  .aggregate(zipperJVM, zipperJS)
  .settings(commonSettings)
  .settings(
    name := "zipper-root",
    publish := {},
    publishLocal := {},
    publishArtifact := false
  )
