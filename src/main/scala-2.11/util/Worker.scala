package util

import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContextExecutorService

/**
  * Created by xinszhou on 6/14/16.
  */

object Worker {
  val log = LoggerFactory.getLogger(getClass)
}

/**
  * 一个线程池的 wrapper
  * @param executionContextExecutorService
  */
class Worker(val executionContextExecutorService: ExecutionContextExecutorService) {
  import Worker.log

  def action(f: => Unit): Unit = {
    this.executionContextExecutorService.execute(new Runnable {
      override def run(): Unit = {
        try {
          f
        } catch {
          case e: Exception =>
            log.error("Failed to execute task %s".format(f), e)
        }
      }
    })
  }

  def shutdown: Unit = {
    this.executionContextExecutorService.shutdown()
  }

}
