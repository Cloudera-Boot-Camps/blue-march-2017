package bootcamp.streaming.executor

import java.util

import bootcamp.SlackWrap
import kafka.serializer.StringDecoder
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Try

object MeasureUtils {
  val separator = ","
  def getMeasure(in: String) : Try[Measure] = {
    Try( {
      val tokens = in.split(separator)
      new Measure(tokens(0), tokens(1).toInt,tokens(2).toInt,tokens(3).toInt,tokens(4).toLong,tokens(5).toFloat,tokens(6).toFloat,tokens(6).toFloat)
    }
    )
  }
}

case class Measure(
                    measurement_id: String,
                    detector_id: Int,
                    galaxy_id: Int,
                    astrophysicist_id: Int,
                    measurement_time: Long,
                    amplitude_1: Float,
                    amplitude_2: Float,
                    amplitude_3: Float) {

  def isAnomaly = (amplitude_1>0.995 && amplitude_3 > 0.995 && amplitude_2 < 0.005)
}

import MeasureUtils._

object FCEStreaming {

  private val AppName = "FCEStreaming"

  // TODO - Implement checkponting
  // TODO - Manage exceptions

  def execute(master: Option[String],
              streamingParameters: Map[String,String],
              topics: Set[String],
              kafkaParameters: Map[String,String]): Unit = {
    val sc = {
      val conf = new SparkConf().setAppName(AppName)
      for (m <- master) {
        conf.setMaster(m)
      }
      new SparkContext(conf)
    }

    val microBatchRateSecs = streamingParameters.getOrElse("streamingRate","10").toLong
    val ssc = new StreamingContext(sc, Seconds(microBatchRateSecs))

    // Kafka parameters
    val kafkaStream: DStream[(String, String)] = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParameters,topics)

    val allMeasures = kafkaStream.map( in => (in._2,getMeasure(in._2)))
    val goodMeasures = allMeasures.filter(_._2.isSuccess).map(_._2.get)

    //Find anomalies and send to slack.
    val anomalies = goodMeasures.filter(m => m.isAnomaly)

    anomalies.foreachRDD(an => {

      an.foreachPartition(p => {
        val props = new util.HashMap[String, Object]()
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "ip-172-31-9-124.us-west-2.compute.internal:9092,ip-172-31-5-78.us-west-2.compute.internal:9092,ip-172-31-4-187.us-west-2.compute.internal:9092,ip-172-31-12-6.us-west-2.compute.internal:9092")
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
          "org.apache.kafka.common.serialization.StringSerializer")
        val producer = new KafkaProducer[String, String](props)

        p.foreach(a => {
          try
            producer.send(new ProducerRecord[String, String]("anomalies", null, a.toString))
          catch {
            case e: Exception => println("AnExc" + e.getMessage)
          }
        })

        producer.close

      })

    })

    //val badMeasures = allMeasures.filter(_._2.isFailure).map(_._1)

    // Start Spark Streaming Context
    ssc.start()
    ssc.awaitTermination()

  }

}
