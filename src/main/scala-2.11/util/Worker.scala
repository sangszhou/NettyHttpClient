package util

import java.util.concurrent.ExecutorService

import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

/**
  * Created by xinszhou on 6/14/16.
  *
  *
  * worker is used to sequelize http request, it works like lock
  * worker is one, but available connections are many
  *
  */

object Worker {
  val log = LoggerFactory.getLogger(getClass)

  def apply(): Worker = apply(ExecutorServiceUtils.newFixedPool(1, "worker-thread"))

  def apply(executorService: ExecutorService): Worker = {
    new Worker(ExecutionContext.fromExecutorService(executorService))
  }

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
