package util

import java.util.concurrent.{Executors, ThreadFactory}
import java.util.concurrent.atomic.AtomicInteger

/**
  * Created by xinszhou on 6/14/16.
  */

/**
  * set daemon 的作用什么
  * factory 的作用是起名字
  * @param name
  */
case class DaemonThreadFactory(name: String) extends ThreadFactory{

  private val threadNumber = new AtomicInteger(1)
  override def newThread(runnable: Runnable): Thread = {
    val thread = Executors.defaultThreadFactory().newThread(runnable)
    thread.setDaemon(true)
    val threadName = name + "-thread-" + threadNumber.getAndIncrement()
    thread.setName(threadName)
    thread
  }
}
