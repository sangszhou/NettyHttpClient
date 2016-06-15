package connectionTest

import httpclient.HttpConnection$
import org.scalatest.FunSuite
import pool.ConnectionPool
import testUtil.{HttpRequestFactory, LocalEsConnectionPool}

/**
  * Created by xinszhou on 6/15/16.
  */
class PoolCallLocalEsSpec extends FunSuite {

  test("create pool call local es server") {
    LocalEsConnectionPool.pooledAction { pool =>
      pool.sendQuery(HttpRequestFactory.getLocalESMeta)
    }

    Thread.sleep(3000)
  }


  test("concurrent request to es server should not throw exception") {
    LocalEsConnectionPool.pooledAction { pool =>
      pool.sendQuery(HttpRequestFactory.getLocalESMeta)
      pool.sendQuery(HttpRequestFactory.getLocalESMeta)
    }

    Thread.sleep(3000)
  }


}
