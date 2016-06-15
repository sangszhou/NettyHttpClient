package testUtil

import common.Connection
import httpclient.HttpClientConnection
import pool.{HttpConnectionFactory, ConnectionPool}
import util.GlobalConfig

/**
  * Created by xinszhou on 6/15/16.
  */
object LocalEsConnectionPool {

  def pooledAction(f: ConnectionPool[HttpClientConnection] => Unit) = {

    val factory = new HttpConnectionFactory(GlobalConfig.esServer)
    val connectionPool = new ConnectionPool[HttpClientConnection](factory)

    f(connectionPool)
  }
}
