package bootcamp

import java.math.BigInteger

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Alternative data import from oracle to a set of parquet files.  Uses JDBC in spark rather than sqoop.
  * There is no replacement for direct mode in sqoop so an index was built on the measurements table.
  * <p>
  *   CREATE INDEX MEASUREMENTS_MEASUREMENT_TIME ON MEASUREMENTS(MEASUREMENT_TIME) TABLESPACE users;
  * </p>
  *
  */
object SparkJDBCImport {
  val AppName = "AstroImportJDBC"
  val conStr = "jdbc:oracle:thin:@bootcamp-march2017.cghfmcr8k3ia.us-west-2.rds.amazonaws.com:15210:gravity"
  val driver = "oracle.jdbc.driver.OracleDriver"
  val user = "gravity"
  val pwd = "gravity"

  val tables = Array("ADMIN.ASTROPHYSICISTS", "ADMIN.GALAXIES", "ADMIN.DETECTORS", "ADMIN.MEASUREMENTS")
  val basePath = "/user/ec2-user/sparkimport/"

  def execute(master: Option[String], args: List[String], jars: Seq[String] = Nil): Unit = {
    val sc = {
      val conf = new SparkConf().setAppName(AppName)
      for (m <- master) {
        conf.setMaster(m)
      }
      new SparkContext(conf)
    }
    val sqlContext = new SQLContext(sc)

    for (t <- tables) {

      t match {
        case "ADMIN.MEASUREMENTS" =>
          val lowerBound = longValue("(select min(measurement_time) from admin.measurements)", sqlContext)
          val upperBound = longValue("(select max(measurement_time) from admin.measurements)", sqlContext)

          val opts = Map("partitionColumn" -> "MEASUREMENT_TIME",
            "lowerBound" -> lowerBound.toString,
            "upperBound" -> upperBound.toString,
            "numPartitions" -> "16",
            "fetchSize" -> "5000")
          importTable(t, basePath + t, sqlContext, opts)
        case _ =>
          importTable(t, basePath + t, sqlContext, Map.empty)
      }
    }
  }

  def importTable(tableName: String, savePath: String, sqlContext: SQLContext,
                  addtlOpts: Map[String, String]): Unit = {
    val opts = baseOpts(tableName) ++ addtlOpts

    val df = sqlContext.read.format("jdbc")
      .options(opts)
      .load

    df.write.option("compression","snappy").parquet(savePath)
  }

  def longValue(sql: String, sqlContext: SQLContext): BigInteger = {
    val df = sqlContext.read.format("jdbc")
      .options(baseOpts(sql))
      .load
    df.collect()(0).getDecimal(0).toBigInteger
  }

  def baseOpts(statement: String): Map[String, String] = {
    Map(
      "url" -> conStr,
      "dbtable" -> statement,
      "driver" -> driver,
      "user" -> user,
      "password" -> pwd)
  }
}