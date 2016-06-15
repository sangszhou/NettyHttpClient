package pool

import common.{Configuration, Connection}
import httpclient.HttpClientConnection
import org.slf4j.LoggerFactory
import util.FutureUtils
import scala.concurrent.duration._
import scala.util.Try

/**
  * Created by xinszhou on 6/15/16.
  */

object HttpConnectionFactory {
  final val log = LoggerFactory.getLogger(getClass)
}

class HttpConnectionFactory(configuration: Configuration) extends ObjectFactory[HttpClientConnection] {

  import HttpConnectionFactory._

  override def create: HttpClientConnection = {
    val connection = new HttpClientConnection(configuration)
    FutureUtils.awaitFuture(connection.connect)
    connection
  }

  override def destroy(item: HttpClientConnection): Unit =
    try {
    item.disconnect
  } catch {
    case e: Exception =>
      log.error("Failed to close the connection", e)
  }

  override def validate(item: HttpClientConnection): Try[HttpClientConnection] = {
    Try {
      if(!item.isConnected) {
        throw new Exception("Not connected")
      }
      item
    }
  }

}
