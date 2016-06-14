package httpclient

import java.util.concurrent.atomic.{AtomicLong, AtomicReference}

import common.{Configuration, Connection}
import io.netty.channel.{ChannelHandlerContext, EventLoopGroup}
import io.netty.handler.codec.http.{HttpRequest, HttpResponse}
import org.slf4j.LoggerFactory
import util.{ExecutorServiceUtils, NettyUtils}

import scala.concurrent.{ExecutionContext, Future, Promise}

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
                            implicit val executionContext: ExecutionContext = ExecutorServiceUtils.CachedExecutionContext
                          )
  extends Connection
  with EventConnectionDelegate
{

  import HttpClientConnection._

  private final val connectionCount = HttpClientConnection.Counter.incrementAndGet()
  private final val connectionId = s"[http-connection-$connectionCount]"
  private final val connectionPromise = Promise[Connection]
  private final val discoonnectionPromise = Promise[Connection]

  private val queryPromiseReference = new AtomicReference[Option[Promise[HttpResponse]]]()

  private var connected = false

  private final val connectionHandler = new HttpConnectionHandler (
    this,
    configuration,
    group,
    executionContext,
    connectionId
    )

  override def disconnect: Future[Connection] = ???

  override def isConnect: Boolean = this.connected

  override def sendQuery(query: HttpRequest): Future[String] = {
    val promise = Promise[String]
    this.connectionHandler.write(query)
    promise.future
  }

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

  override def onMessageReceived(result: String): Unit = {
    log.info("message received")
  }

  private def succeedQueryPromise(queryResult: HttpResponse) = {
    this.clearQueryPromise.foreach {
      _.success(queryResult)
    }
  }

  private def setQueryPromise(promise: Promise[HttpResponse]) = {
    if (!this.queryPromiseReference.compareAndSet(None, Some(promise)))
      throw new Exception("Query not returned yet exception")
  }
  private def clearQueryPromise: Option[Promise[HttpResponse]] = {
    this.queryPromiseReference.getAndSet(None)
  }
}
