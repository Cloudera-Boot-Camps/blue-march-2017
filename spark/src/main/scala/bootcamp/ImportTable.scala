package bootcamp

import org.apache.spark.sql.{DataFrame, SQLContext}
//import org.apache.spark.sql.hive.HiveContext

/**
  * Created by cfusi on 29/03/2017.
  */
case class TableInfo(val db: String, val tableName: String) {
  def fullName = s"${db}.${tableName}"
}

object ImportTable {

  /*
  importTable(sqlContext,
  "oracle.jdbc.driver.OracleDriver",
  "jdbc:oracle:thin:@bootcamp-march2017.cghfmcr8k3ia.us-west-2.rds.amazonaws.com:15210:gravity",
  "gravity","gravity",
  TableInfo("ADMIN","ASTROPHYSICISTS"),
  TableInfo("presentation","claudio"))
   */
  def importTable(sqlContext: SQLContext,
                  jdbcDriver: String,
                  jdbcString: String, jdbcUser: String, jdbcPassword: String,
                  inTable: TableInfo, hiveTable: TableInfo) : DataFrame = {
    val df = sqlContext.read.format("jdbc").
      options(Map(
        "url" -> jdbcString,
        "dbtable" -> inTable.fullName,
        "driver" -> jdbcDriver,
        "user" -> jdbcUser,
        "password" -> jdbcPassword)).
      load()

    df.show()
    df
  }
}
