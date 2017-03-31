package bootcamp

import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory

object SlackWrap {

  def postMessage(channelName: String, message: String): Unit = {
    val session = SlackSessionFactory.createWebSocketSlackSession("xoxp-161768544950-161092983106-162347731010-9c5a189b3873acafa8bf4bdc409e5960")
    session.connect()

    val channel = session.findChannelByName(channelName)
    session.sendMessage(channel, message)
    session.disconnect
  }
}
