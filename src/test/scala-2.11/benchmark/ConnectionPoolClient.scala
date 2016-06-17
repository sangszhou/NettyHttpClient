package benchmark

import httpclient.HttpConnection
import org.scalatest.FunSuite
import pool.{ConnectionPoolThreadSafe, HttpConnectionFactory}
import testUtil.HttpRequestFactory
import util.{FutureUtils, GlobalConfig}

import scala.collection.mutable.ListBuffer

/**
  * Created by xinszhou on 6/15/16.
  */
object ConnectionPoolClient extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val startTime = System.currentTimeMillis()


  val factory = new HttpConnectionFactory(GlobalConfig.esServer)
  val connectionPool = new ConnectionPoolThreadSafe[HttpConnection](factory)

  val threadList = new ListBuffer[Thread]

  var i = 0
  while (i < 3) {
    val thread: Thread = new Thread(new Runnable {
      override def run(): Unit = {
        val responseEntity = connectionPool.sendQuery(HttpRequestFactory.getLocalESMeta)
//        responseEntity.map(_ => println(s"response got: ${System.currentTimeMillis()}")) // 这句话的执行要晚于 awaitFuture
        FutureUtils.awaitFuture(responseEntity)
      }
    })

    threadList += thread
    i += 1
  }


  println(s"start time: $startTime")


  threadList.toList.foreach(_.start())
  threadList.toList.foreach(_.join())

  val endTime = System.currentTimeMillis()



  println(s"time diff is: ${endTime - startTime}")

    Thread.sleep(3000)
}
