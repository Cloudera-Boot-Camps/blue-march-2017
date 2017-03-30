package bootcamp

import org.apache.spark.SparkContext

object FCEMainJob {
  def main(args: Array[String]) {

    // Run the word count
    FCEMain.execute(
      master    = None,
      args      = args.toList,
      jars      = List(SparkContext.jarOfObject(this).get)
    )

    // Exit with success
    System.exit(0)
  }
}