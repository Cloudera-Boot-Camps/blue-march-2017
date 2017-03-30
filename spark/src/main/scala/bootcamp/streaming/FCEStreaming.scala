package bootcamp.streaming

import kafka.serializer.StringDecoder
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
              kafkaParameters: Map[String,String],
              jars: Seq[String] = Nil): Unit = {
    val sc = {
      val conf = new SparkConf().setAppName(AppName).setJars(jars)
      for (m <- master) {
        conf.setMaster(m)
      }
      new SparkContext(conf)
    }

    val microBatchRateSecs = streamingParameters.getOrElse("streamingRate","10").toLong
    val ssc = new StreamingContext(sc, Seconds(microBatchRateSecs))

    // Kafka parameters


    val kafkaStream: DStream[(String, String)] = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParameters,topics)

    // val allMeasures = kafkaStream.map( in => (in._2,MeasurementString.unapply(in._2)))

    val allMeasures = kafkaStream.map( in => (in._2,getMeasure(in._2)))

    val goodMeasures = allMeasures.filter(_._2.isSuccess).map(_._2)

    val badMeasures = allMeasures.filter(_._2.isFailure).map(_._1)

    goodMeasures.print()


    // Start Spark Streaming Context
    ssc.start()
    ssc.awaitTermination()

  }

}
