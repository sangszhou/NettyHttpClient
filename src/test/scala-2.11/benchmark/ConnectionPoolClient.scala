package benchmark

import httpclient.HttpConnection
import org.scalatest.FunSuite
import pool.{ConnectionPool, HttpConnectionFactory}
import testUtil.HttpRequestFactory
import util.{FutureUtils, GlobalConfig}

import scala.collection.mutable.ListBuffer

/**
  * Created by xinszhou on 6/15/16.
  */
object ConnectionPoolClient extends App {


  val factory = new HttpConnectionFactory(GlobalConfig.esServer)
  val connectionPool = new ConnectionPool[HttpConnection](factory)


  val threadList = new ListBuffer[Thread]

  var i = 0
  while (i < 11) {
    val thread: Thread = new Thread(new Runnable {
      override def run(): Unit = {
        val responseEntity = connectionPool.sendQuery(HttpRequestFactory.getLocalESMeta)
        FutureUtils.awaitFuture(responseEntity)
      }
    })

    threadList += thread
    i += 1
  }


  val startTime = System.currentTimeMillis()

  threadList.toList.foreach(_.start())
  threadList.toList.foreach(_.join())

  val endTime = System.currentTimeMillis()

  println(s"time diff is: ${endTime - startTime}")



}
