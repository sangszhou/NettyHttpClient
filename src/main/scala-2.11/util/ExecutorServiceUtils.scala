package util

import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.ExecutionContext

/**
  * Created by xinszhou on 6/14/16.
  */
object ExecutorServiceUtils {
  implicit val CachedThreadPool = Executors.newCachedThreadPool(DaemonThreadFactory("httpclient-netty"))
  implicit val CachedExecutionContext = ExecutionContext.fromExecutor(CachedThreadPool)

  def newFixedPool(count: Int, name: String): ExecutorService = {
    Executors.newFixedThreadPool(count, DaemonThreadFactory(name))
  }

}
