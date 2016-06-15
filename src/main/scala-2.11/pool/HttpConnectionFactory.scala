package pool

import common.{Configuration, Connection}
import httpclient.HttpConnection
import org.slf4j.LoggerFactory
import util.FutureUtils

import scala.util.Try

/**
  * Created by xinszhou on 6/15/16.
  */

object HttpConnectionFactory {
  final val log = LoggerFactory.getLogger(getClass)
}

class HttpConnectionFactory(configuration: Configuration) extends ObjectFactory[HttpConnection] {

  import HttpConnectionFactory._

  override def create: HttpConnection = {
    val connection = new HttpConnection(configuration)
    FutureUtils.awaitFuture(connection.connect)
    connection
  }

  override def destroy(item: HttpConnection): Unit =
    try {
    item.disconnect
  } catch {
    case e: Exception =>
      log.error("Failed to close the connection", e)
  }

  override def validate(item: HttpConnection): Try[HttpConnection] = {
    Try {
      if(!item.isConnected) {
        throw new Exception("Not connected")
      }
      item
    }
  }

}
