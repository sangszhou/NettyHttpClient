package testUtil

import common.Connection
import httpclient.HttpConnection
import util.{FutureUtils, GlobalConfig}

import scala.concurrent.Future

/**
  * Created by xinszhou on 6/15/16.
  */
object LocalESConnection {

  //@clear connection after using it
  def oneShoot(f: Connection => Unit) = {
    val connection = new HttpConnection(GlobalConfig.esServer)
    FutureUtils.awaitFuture(connection.connect)
    try {
      f(connection)
    }
  }
}
