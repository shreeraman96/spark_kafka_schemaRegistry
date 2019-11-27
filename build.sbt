name := "schema_reg_latest"

version := "0.1"

scalaVersion := "2.11.8"

resolvers += "confluent" at "https://packages.confluent.io/maven/"

excludeDependencies ++= Seq(
  // commons-logging is replaced by jcl-over-slf4j
  ExclusionRule("com.fasterxml.jackson.core","jackson-databind")
)

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.4" //exclude("com.fasterxml.jackson.core","jackson-databind")

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.3.4"

libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.3.4"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.3.4" % "provided"

libraryDependencies += "org.apache.kafka" % "kafka-clients" % "1.1.1"

libraryDependencies += "com.typesafe" % "config" % "1.3.3"

libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.5"

libraryDependencies += "com.vividsolutions" % "jts" % "1.13"

libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.0" //% "provided"

libraryDependencies += "org.apache.kafka" %% "kafka" % "0.8.2.0"

libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "5.1.0"

libraryDependencies += "io.confluent" % "common-config" % "5.1.0"

libraryDependencies += "io.confluent" % "kafka-schema-registry-client" % "5.2.1"

libraryDependencies += "za.co.absa" %% "abris" % "3.0.3"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.1" % "compile"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.7.4"

libraryDependencies += "net.liftweb" %% "lift-json" % "3.4.0"

libraryDependencies += "org.apache.spark" %% "spark-avro" % "2.4.4"

libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "3.7.0"
