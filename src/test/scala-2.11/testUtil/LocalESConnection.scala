package testUtil

import common.Connection
import httpclient.HttpClientConnection
import util.{FutureUtils, GlobalConfig}

import scala.concurrent.Future

/**
  * Created by xinszhou on 6/15/16.
  */
object LocalESConnection {

  //@clear connection after using it
  def oneShort(f: Connection => Unit) = {
    val connection = new HttpClientConnection(GlobalConfig.esServer)
    FutureUtils.awaitFuture(connection.connect)
    f(connection)
  }
}
