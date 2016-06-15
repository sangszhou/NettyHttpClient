package util

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
/**
  * Created by xinszhou on 16/6/14.
  */
object FutureUtils {
  def awaitFuture[T]( future : Future[T] ) : T = {
    Await.result(future, 500 seconds )
  }
}
