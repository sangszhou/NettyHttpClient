package testUtil

import common.Connection
import httpclient.HttpConnection
import pool.{HttpConnectionFactory, ConnectionPool}
import util.GlobalConfig

/**
  * Created by xinszhou on 6/15/16.
  */
object LocalEsConnectionPool {

  def pooledAction(f: ConnectionPool[HttpConnection] => Unit) = {

    val factory = new HttpConnectionFactory(GlobalConfig.esServer)
    val connectionPool = new ConnectionPool[HttpConnection](factory)

    f(connectionPool)
  }
}
