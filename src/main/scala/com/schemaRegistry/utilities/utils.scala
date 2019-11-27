package com.schemaRegistry.utilities

import za.co.absa.abris.avro.read.confluent.SchemaManager
import za.co.absa.abris.avro.functions.{to_avro,from_avro}
import com.schemaRegistry.utilities.Constants
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import za.co.absa.abris.avro.parsing.utils.AvroSchemaUtils
import za.co.absa.abris.avro.read.confluent


object utils {

    def getSchemaRegistryConfig():Map[String,String] = {

        //setting up the configuration for the schema registry
        val schemaRegistryConf = Map(
            //setting up the configuration for schema registry
            SchemaManager.PARAM_SCHEMA_REGISTRY_URL -> Constants.schema_registry_url,
            SchemaManager.PARAM_VALUE_SCHEMA_NAMING_STRATEGY -> SchemaManager.SchemaStorageNamingStrategies.TOPIC_RECORD_NAME,
            SchemaManager.PARAM_SCHEMA_REGISTRY_TOPIC -> Constants.kafka_topic,
            SchemaManager.PARAM_SCHEMA_NAME_FOR_RECORD_STRATEGY      -> Constants.schema_name,
            SchemaManager.PARAM_SCHEMA_NAMESPACE_FOR_RECORD_STRATEGY -> Constants.schema_namespace,
            SchemaManager.PARAM_VALUE_SCHEMA_ID -> "latest"
        )
        schemaRegistryConf
    }

    def writeAvro(dataFrame: DataFrame, schemaRegistryConfig: Map[String, String]) = {

        val allColumns = struct(dataFrame.columns.head, dataFrame.columns.tail: _*)
        dataFrame.select(to_avro(allColumns, schemaRegistryConfig) as 'value).write
          .format("kafka")
          .option("kafka.bootstrap.servers",Constants.kafka_bootstrap_server)
          .option("topic",Constants.kafka_topic)
          .save()
    }

    def registerAvroSchema() = {

        // test function to register the schema and check if its up and running fine
        var config = Map(
            SchemaManager.PARAM_SCHEMA_REGISTRY_URL                  -> Constants.schema_registry_url,
            SchemaManager.PARAM_VALUE_SCHEMA_NAMING_STRATEGY         -> SchemaManager.SchemaStorageNamingStrategies.TOPIC_RECORD_NAME,
            SchemaManager.PARAM_SCHEMA_NAME_FOR_RECORD_STRATEGY      -> Constants.schema_name,
            SchemaManager.PARAM_SCHEMA_NAMESPACE_FOR_RECORD_STRATEGY -> Constants.schema_namespace
        )

        SchemaManager.configureSchemaRegistry(config)
        val topic = Constants.schema_topic
        val schema = AvroSchemaUtils.load(Constants.sampleSchema)
        val subject = SchemaManager.getSubjectName(topic,false,schema,config)
        val schemaId = SchemaManager.register(schema,"test_sub")
    }

}
