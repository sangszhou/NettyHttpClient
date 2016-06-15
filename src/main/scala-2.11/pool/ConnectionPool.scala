package pool

import java.util.concurrent.ExecutorService

import common.Connection
import io.netty.handler.codec.http.{HttpRequest, HttpResponse}
import util.ExecutorServiceUtils

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by xinszhou on 6/15/16.
  */
class ConnectionPool[T <: Connection](
                                           factory: ObjectFactory[T],
                                           executionContext: ExecutionContext = ExecutorServiceUtils.CachedExecutionContext
                                         )
  extends ObjectPool[T](factory) with Connection {


  override def disconnect: Future[Connection] = {
    if (this.isConnected) {
      this.close.map(item => this)(executionContext)
    } else Future.successful(this)
  }

override def isConnected: Boolean = ! this.isClosed

override def sendQuery (query: HttpRequest): Future[HttpResponse] = this.use(_.sendQuery(query))(executionContext)

override def connect: Future[Connection] = Future.successful(this)
}
