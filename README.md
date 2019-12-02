# spark_kafka_schemaRegistry
A sample project to showcase how to use Schema registry and Kafka to stream structured data with schema

One of the most tedious processes in processing streaming data using kafka and spark is deserializing the data and processing each data in the byte stream to form a structured dataframe.

Confluent platform has a schema registry which we can use along with avro data format to bypass the tedious process. Herein, through this technique the data is being transferred in avro format with the schema being stored in the schema registry server. Both producer and consumer kafka has access to the registry and hence can retrieve the corresponding schema to deserialize the data received in the receiving end.

A fair knowledge on how to use docker and write code in spark-scala is necessary, however detailed steps on how to setup and use Spark, Kafka and Schema registry to send and receive data has been covered.

The key concepts and definitions can be found here in the [wiki page](https://github.com/shreeraman96/spark_kafka_schemaRegistry/wiki/Key-terms-and-definition)

**Setting up the environment** 
To setup the environment needed for this project, detailed instructions has been added for both windows and linux users in this particular [page](https://github.com/shreeraman96/spark_kafka_schemaRegistry/wiki/Setting-up-the-environment)

#### Breakdown and implementation of the code

sample data that we are going to be use can be found [here](https://github.com/shreeraman96/spark_kafka_schemaRegistry/blob/master/src/main/resources/dataset/sampledata.csv). Schema for the same can be found [here](https://github.com/shreeraman96/spark_kafka_schemaRegistry/blob/master/src/main/resources/schema/sample.avsc)


**Setting up spark session:**

    import org.apache.spark.SparkConf
    import org.apache.spark.sql.SparkSession

*setting up spark configuration for the spark session*
    
    val conf: SparkConf = new SparkConf()

*kryo serializer for spark serialization/deserialization*

    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

*using spark locally*

    conf.setMaster("local[*]")
    val spark = SparkSession.builder.appName("Read and write with schema").config(conf).getOrCreate()

**Reading the sample data file**

    val sampleData = spark.read.format("csv").option("header","true").option("inferSchema","true").load(Constants.sampleDataFile)

**Defining a function to get schema registry configuration map**

*setting up the configuration for schema registry with*

* Schema registry url
* subject naming strategy
* Kafka topic
* Name space for record
* Schema version to fetch 
    
       def getSchemaRegistryConfig():Map[String,String] = {
           //setting up the configuration for the schema registry
           val schemaRegistryConf = Map(    
           SchemaManager.PARAM_SCHEMA_REGISTRY_URL -> Constants.schema_registry_url,
           SchemaManager.PARAM_VALUE_SCHEMA_NAMING_STRATEGY -> SchemaManager.SchemaStorageNamingStrategies.TOPIC_RECORD_NAME,
           SchemaManager.PARAM_SCHEMA_REGISTRY_TOPIC -> Constants.kafka_topic,
           SchemaManager.PARAM_SCHEMA_NAME_FOR_RECORD_STRATEGY      -> Constants.schema_name,
           SchemaManager.PARAM_SCHEMA_NAMESPACE_FOR_RECORD_STRATEGY -> Constants.schema_namespace,
           SchemaManager.PARAM_VALUE_SCHEMA_ID -> "latest"
           )
           schemaRegistryConf
        }

**Creating a new registry configuration map**

``val schemaRegistryConfig = utils.getSchemaRegistryConfig()``

For avro serialization and deserialization I have utilized the ABRIS- Avro Bridge for Spark. The library can be found [here](https://github.com/AbsaOSS/ABRiS)

**writing dataframe into kafka as avro**

    def writeAvro(dataFrame: DataFrame, schemaRegistryConfig: Map[String, String]) = {
    // function to convert dataframe into avro data and write into kafka topic 
       val allColumns = struct(dataFrame.columns.head, dataFrame.columns.tail: _*)
       dataFrame.select(to_avro(allColumns, schemaRegistryConfig) as 'value).write
                .format("kafka")
                .option("kafka.bootstrap.servers",Constants.kafka_bootstrap_server)
                .option("topic",Constants.kafka_topic)
                .save()
    }

**writing our actual data frame using the above defined function**

`` utils.writeAvro(sampleData,schemaRegistryConfig) ``

**Reading streaming avro data from kafka through spark and schema registry**

    val input = spark.readStream
                     .format("kafka")
                     .option("kafka.bootstrap.servers", Constants.kafka_bootstrap_server)
                     .option("subscribe", Constants.kafka_topic)
                     .option("startingOffsets", "latest")
                     .option("failOnDataLoss", "false")
                     .load()

**converting byte streams into AVRO using from_avro**

    val outputDf = spark.readStream
                        .format("kafka")
                        .option("kafka.bootstrap.servers", Constants.kafka_bootstrap_server)
                        .option("subscribe", Constants.kafka_topic)
                        .option("startingOffsets", "latest")
                        .load()
                        .selectExpr("CAST(key as STRING)", "value")
                        .select(from_avro(col("value"),schemaRegConfig) as "data")
                        .select("data.*")

**writing the streaming dataframe into console**

``outputDf.writeStream.format("console").start().awaitTermination()``


For any clarifications and doubts feel free to contact me through LinkedIn :  
[Shreeraman A K](https://www.linkedin.com/in/shreeraman-karikalan/)
