package com.schemaRegistry.config

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

trait SparkEngine extends App {

  val conf: SparkConf = new SparkConf()
  conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  conf.setMaster("local[*]")

  val spark = SparkSession.builder.appName("Read and write with schema").config(conf).getOrCreate()

}
