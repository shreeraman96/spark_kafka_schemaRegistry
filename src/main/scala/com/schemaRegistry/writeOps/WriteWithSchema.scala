package com.schemaRegistry.writeOps

import com.schemaRegistry.config.SparkEngine
import com.schemaRegistry.utilities.Constants
import com.schemaRegistry.utilities.utils
import za.co.absa.abris.avro.AvroSerDeWithKeyColumn._

object WriteWithSchema extends SparkEngine{

    val sampleData = spark.read.format("csv").option("header","true").option("inferSchema","true").load(Constants.sampleDataFile)

    //getting the configurations for schema registry
    val schemaRegistryConfig = utils.getSchemaRegistryConfig()

    //writing the sample data to the kafka using avro schema registry
    utils.writeAvro(sampleData,schemaRegistryConfig)

}
