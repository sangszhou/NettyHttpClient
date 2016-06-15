package pool

import util.Worker
import java.util

import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Queue, Stack}
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
  * Created by xinszhou on 6/15/16.
  *
  * be careful about concurrent actions
  */
class ObjectPool[T](
                     factory: ObjectFactory[T]
                   ) extends AsyncObjectPool[T] {

  final val log = LoggerFactory.getLogger(getClass)

  //defines how many concurrent tasks can be executed
  private val mainPool = Worker()

  //store items that will be available in the future
  private val waitQueue = new Queue[Promise[T]]()

  //store items that are doing query task
  private val checkouts = new ArrayBuffer[T](10)

  //available connections
  private val poolable = new Stack[T]()

  private var closed = false


  override def take: Future[T] = {
    if(this.closed) {
      Promise.failed(new Exception("failed to take item from pool")).future
    } else {
      val promise = Promise[T]()
      this.checkout(promise)
      promise.future
    }
  }

  private def checkout(promise: Promise[T]) = {
    this.mainPool.action {
      if (this.isFull) {
        this.enqueuePromise(promise)
      } else this.createOrReturnItem(promise)
    }
  }

  //all available
  private def isFull: Boolean = {
    val fullStatus = this.poolable.isEmpty && this.checkouts.size == 10


    fullStatus
  }

  //@todo wait queue size should be configurable
  private def enqueuePromise(promise: Promise[T]) = {
    if (this.waitQueue.size >= 100) {
      val exception = new Exception("Waiting queue is full")
      exception.printStackTrace()
      promise.tryFailure(exception)
    } else this.waitQueue += promise
  }

  private def createOrReturnItem(promise: Promise[T]) = {
    if (this.poolable.isEmpty) {
      try {
        val item: T = this.factory.create
        this.checkouts += item
        promise.success(item)
      } catch {
        case e: Exception => promise.failure(e)
      }
    } else {
      val item: T = this.poolable.pop()
      this.checkouts += item
      promise.success(item)
    }
  }

  private def addBack(item: T, promise: Promise[AsyncObjectPool[T]]) = {
    this.poolable.push(item)

    if (this.waitQueue.nonEmpty) {
      this.checkout(this.waitQueue.dequeue())
    }

    promise.success(this)
  }

  override def giveBack(item: T): Future[AsyncObjectPool[T]] = {

    val promise = Promise[AsyncObjectPool[T]]

    this.mainPool.action {

      val idx = this.checkouts.indexOf(item)

      this.checkouts.remove(idx)

      this.factory.validate(item) match {
        case Success(item) => {
          this.addBack(item, promise)
        }
        case Failure(e) => {
          this.factory.destroy(item)
          promise.failure(e)
        }
      }
    }

    promise.future
  }

  override def close: Future[AsyncObjectPool[T]] = {
    try {
      val promise = Promise[AsyncObjectPool[T]]
      this.mainPool.action {
        if(!this.closed) {
          try {
            this.mainPool.shutdown
            this.closed = true
            (this.poolable.map(i => i) ++ this.checkouts).foreach(item =>
              factory.destroy(item))
            promise.success(this)
          } catch {
            case exp: Exception => promise.failure(exp)
          }
        }
      }
      promise.future
    } catch {
      case e: Exception if (this.closed) =>
        Future.successful(this)
    }
  }

  def inUse: Traversable[T] = this.checkouts
  def availables: Traversable[T] = this.poolable
  def queued: Traversable[Promise[T]] = this.waitQueue
  def isClosed: Boolean = this.closed


  override def finalize() = {
    this.close
  }

}
