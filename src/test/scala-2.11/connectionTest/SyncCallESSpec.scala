package connectionTest

import com.typesafe.config.ConfigFactory
import common.Configuration
import httpclient.HttpClientConnection
import org.scalatest.FunSuite
import testUtil.{HttpRequestFactory, LocalESConnection}
import util.{FutureUtils, GlobalConfig}

/**
  * Created by xinszhou on 16/6/14.
  */
class SyncCallESSpec extends FunSuite {

  test("fetch meta info from es server") {

    val connection = new HttpClientConnection(GlobalConfig.esServer)
    FutureUtils.awaitFuture(connection.connect)
    connection.sendQuery(HttpRequestFactory.getLocalESMeta)

    Thread.sleep(3000)
  }

  //should throw exception
  test("send messages in parallel on one connection would throw exception") {
    LocalESConnection.oneShoot(connection => {
      connection.sendQuery(HttpRequestFactory.getLocalESMeta)
      connection.sendQuery(HttpRequestFactory.getLocalESMeta)
    })

    Thread.sleep(3000)
  }

  //should success
  test("send multi message to connection, but only after the first response received") {
    LocalESConnection.oneShoot(connection => {
      connection.sendQuery(HttpRequestFactory.getLocalESMeta)
      Thread.sleep(3000)
      connection.sendQuery(HttpRequestFactory.getLocalESMeta)
    })

    Thread.sleep(3000)
  }

  //don't know if this requirements is needed yet
  test("send messages in parallel, the second message will be abandoned, but the first one will not be affected") {

  }


}
