name := "scalike-sample"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"       % "2.4.2",
  "mysql" % "mysql-connector-java" % "5.1.39",
  "ch.qos.logback"  %  "logback-classic"   % "1.1.7"
)