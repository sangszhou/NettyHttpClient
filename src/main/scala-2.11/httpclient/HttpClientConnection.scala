package httpclient

import java.util.concurrent.atomic.AtomicLong

import common.{Configuration, Connection}
import io.netty.channel.{ChannelHandlerContext, EventLoopGroup}
import org.slf4j.LoggerFactory
import util.{ExecutorServiceUtils, NettyUtils}

import scala.concurrent.{ExecutionContext, Future}

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

  private final val connectionCount = HttpClientConnection.Counter.incrementAndGet()
  private final val connectionId = s"[http-connection-$connectionCount]"


  private final val connectionHandler = new HttpConnectionHandler (
    this,
    configuration,
    group,
    executionContext,
    connectionId
    )



  override def disconnect: Future[Connection] = ???

  override def isConnect: Boolean = ???

  override def sendQuery(query: String): Future[String] = ???

  override def connect: Future[Connection] = ???

  override def connected(ctx: ChannelHandlerContext): Unit = ???

  override def exceptionCaught(exception: Throwable): Unit = ???

  override def onMessageReceived(result: String): Unit = ???
}
