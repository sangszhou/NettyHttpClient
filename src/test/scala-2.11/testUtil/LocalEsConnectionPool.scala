package testUtil

import common.Connection
import httpclient.HttpConnection
import pool.{HttpConnectionFactory, ConnectionPoolThreadSafe}
import util.GlobalConfig

/**
  * Created by xinszhou on 6/15/16.
  */
object LocalEsConnectionPool {

  def pooledAction(f: ConnectionPoolThreadSafe[HttpConnection] => Unit) = {

    val factory = new HttpConnectionFactory(GlobalConfig.esServer)
    val connectionPool = new ConnectionPoolThreadSafe[HttpConnection](factory)

    f(connectionPool)
  }
}
