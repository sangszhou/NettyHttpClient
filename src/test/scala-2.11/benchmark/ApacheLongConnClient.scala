package benchmark

import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

import scala.collection.mutable.ListBuffer

/**
  * Created by xinszhou on 6/15/16.
  */
object ApacheLongConnClient extends App {

  def startEndCounting = {
    val threadList = new ListBuffer[Thread]

    val startTime = System.currentTimeMillis()

    var i = 0
    while (i < 1000) {
      val thread: Thread = new Thread(new Runnable {
        override def run(): Unit = {
          val responseEntity = restTemplate.getForEntity(url, classOf[String])
        }
      })

      threadList += thread
      i += 1
    }

    threadList.toList.foreach(_.start())
    threadList.toList.foreach(_.join())

    val endTime = System.currentTimeMillis()

    println(s"time diff is: ${endTime - startTime}")


  }

  val url = "http://localhost:9200"


  private val clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
    HttpClientBuilder
      .create
      .setMaxConnPerRoute(50)
      .setMaxConnTotal(100)
      .build)

  val restTemplate = new RestTemplate(clientHttpRequestFactory)


  println("begin warm up")
  startEndCounting
  println("warm up ends")

  startEndCounting


}
