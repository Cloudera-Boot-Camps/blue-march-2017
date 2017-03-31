package bootcamp

import org.apache.spark.SparkContext

object SparkJDBCImportJob {

  def main(args: Array[String]) {
    SparkJDBCImport.execute(
      master    = None,
      args      = args.toList,
      jars      = List(SparkContext.jarOfObject(this).get)
    )

    // Exit with success
    System.exit(0)
  }
}
