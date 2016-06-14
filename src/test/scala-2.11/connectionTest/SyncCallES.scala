package connectionTest

import com.typesafe.config.ConfigFactory
import common.Configuration
import httpclient.HttpClientConnection
import org.scalatest.FunSuite
import testUtil.HttpRequestFactory
import util.{FutureUtils, GlobalConfig}

/**
  * Created by xinszhou on 16/6/14.
  */
class SyncCallES extends FunSuite {

  test("fetch metainfo from es server") {

    val connection = new HttpClientConnection(GlobalConfig.esServer)
    FutureUtils.awaitFuture(connection.connect)
    connection.sendQuery(HttpRequestFactory.getLocalESMeta)

    Thread.sleep(3000)

  }

}
