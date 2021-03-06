package bootcamp.streaming.main

import bootcamp.SlackWrap
import bootcamp.streaming.executor.FCEStreaming


object FCEStreamingMain {
  def main(args: Array[String]) {
    // Run the word count
    FCEStreaming.execute(
      Some("yarn-client"),
      Map[String,String]("streamingRate" -> "10"),
      Set[String]("measurements2"),
      Map[String, String]("metadata.broker.list" -> "ip-172-31-12-6.us-west-2.compute.internal:9092,ip-172-31-4-187.us-west-2.compute.internal:9092,ip-172-31-5-78.us-west-2.compute.internal:9020,ip-172-31-9-124.us-west-2.compute.internal:9092", "auto.offset.reset" -> "largest")
    )

    // Exit with success
    System.exit(0)
  }
}