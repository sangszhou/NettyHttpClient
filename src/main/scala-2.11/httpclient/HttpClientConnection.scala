package httpclient

import java.util.concurrent.atomic.{AtomicLong, AtomicReference}

import common.{Configuration, Connection}
import io.netty.channel.{ChannelHandlerContext, EventLoopGroup}
import io.netty.handler.codec.http.{HttpRequest, HttpResponse}
import org.slf4j.LoggerFactory
import util.{ExecutorServiceUtils, NettyUtils}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

/**
  * Created by xinszhou on 6/14/16.
  */

object HttpClientConnection {
  final val log = LoggerFactory.getLogger(getClass)

  final val Counter = new AtomicLong()

}

class HttpClientConnection(
    configuration: Configuration,
    group: EventLoopGroup = NettyUtils.DefaultEventLoopGroup,
    implicit val executionContext: ExecutionContext = ExecutorServiceUtils.CachedExecutionContext)
  extends Connection
  with EventConnectionDelegate {

  import HttpClientConnection._

  private final val connectionCount = HttpClientConnection.Counter.incrementAndGet()
  private final val connectionId = s"[http-connection-$connectionCount]"
  private final val connectionPromise = Promise[Connection]
  private final val disconnectionPromise = Promise[Connection]

  private val queryPromiseReference = new AtomicReference[Option[Promise[HttpResponse]]](None)

  private var connected = false

  private final val connectionHandler = new HttpConnectionHandler (
    this,
    configuration,
    group,
    executionContext,
    connectionId
    )

  override def disconnect: Future[Connection] = {
    import util.ChannelFutureTransformer._
    if(!this.connected) {
      this.connectionHandler.disconnect.onComplete {
        case Success(res) => this.disconnectionPromise.trySuccess(this)
        case Failure(exp) => this.disconnectionPromise.tryFailure(exp)
      }
    }
    this.disconnectionPromise.future
  }

  override def isConnect: Boolean = this.connected

  override def sendQuery(query: HttpRequest): Future[HttpResponse] = {
    this.validateIsReadyForQuery
    val promise = Promise[HttpResponse]
    this.setQueryPromise(promise)
    this.connectionHandler.write(query)
    promise.future
  }

  /**
    * where this value is set true?
    * @return
    */
  override def connect: Future[Connection] = {
    this.connectionHandler.connect.onFailure {
      case e => this.connectionPromise.tryFailure(e)
    }
    this.connectionPromise.future
  }

  override def connected(ctx: ChannelHandlerContext): Unit = {
    log.debug("Connected to {}", ctx.channel.remoteAddress)
    this.connected = true
  }

  override def exceptionCaught(exception: Throwable): Unit = {
    log.error("exception caught")
  }

  override def onMessageReceived(result: HttpResponse): Unit = {
    log.info("message received")
    succeedQueryPromise(result)
  }

  private def succeedQueryPromise(queryResult: HttpResponse) = {
    log.info("successfully received message from server")
    this.clearQueryPromise.foreach {
      _.success(queryResult)
    }
  }

  private def validateIsReadyForQuery(): Unit = {
    if(isQuerying()) throw new Exception("connection is in query, not ready yet")
  }

  private def isQuerying() = {
    val optionalData: Option[Promise[HttpResponse]] = this.queryPromiseReference.get()
    optionalData.isDefined
  }

  private def setQueryPromise(promise: Promise[HttpResponse]) = {
    if (!this.queryPromiseReference.compareAndSet(None, Some(promise)))
      throw new Exception("Query not returned yet exception")
  }
  private def clearQueryPromise: Option[Promise[HttpResponse]] = {
    this.queryPromiseReference.getAndSet(None)
  }

}
