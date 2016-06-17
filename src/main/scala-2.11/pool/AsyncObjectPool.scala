package pool

import scala.concurrent.{ExecutionContext, Future, Promise}

/**
  * Created by xinszhou on 6/15/16.
  */
trait AsyncObjectPool[T] {

  def take: Future[T]

  def giveBack(item: T): Future[AsyncObjectPool[T]]

  def close: Future[AsyncObjectPool[T]]

  def use[A](f: T => Future[A])(implicit executionContext: ExecutionContext): Future[A] = {
    take.flatMap { item => {
      val p = Promise[A]()

      // calling f might throw exception
      try {
        f(item).onComplete { res =>
          giveBack(item).onComplete { _ => p.complete(res) }
        }
      } catch {
        case error: Throwable =>
          giveBack(item).onComplete { _ => p.failure(error) }
      }

      p.future
    }
    }
  }

}
