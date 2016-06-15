package httpclient

import java.net.InetSocketAddress

import common.Configuration
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBufAllocator
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel._
import io.netty.handler.codec.CodecException
import io.netty.handler.codec.http._
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future, Promise}
import util.ChannelFutureTransformer._
/**
  * Created by xinszhou on 6/14/16.
  */
class HttpConnectionHandler(
                            eventConnectionDelegate: EventConnectionDelegate,
                            configuration: Configuration,
                            group: EventLoopGroup,
                            executionContext: ExecutionContext,
                            connectionId: String) extends SimpleChannelInboundHandler[HttpResponse] {

  private implicit val internalPool = executionContext
  private final val log = LoggerFactory.getLogger(s"[connection-handler]${connectionId}")
  private final val bootstrap = new Bootstrap().group(this.group)
  private final val connectionPromise = Promise[HttpConnectionHandler]

  private var currentContext: ChannelHandlerContext = null;

  def connect: Future[HttpConnectionHandler] = {
    this.bootstrap.channel(classOf[NioSocketChannel])
    this.bootstrap.handler(new ChannelInitializer[io.netty.channel.Channel]() {
      override def initChannel(ch: Channel): Unit = {
        ch.pipeline().addLast(
          new HttpClientCodec(),
          new HttpContentDecompressor(),
          new HttpObjectAggregator(512 * 1024),
          HttpConnectionHandler.this) // 把自己加到里面去?
      }

    })

    this.bootstrap.option[java.lang.Boolean](ChannelOption.SO_KEEPALIVE, true)

    this.bootstrap.connect(new InetSocketAddress(configuration.host, configuration.port)).onFailure {
      case exception => this.connectionPromise.tryFailure(exception)
    }

    //success 的部分在别处
    this.connectionPromise.future
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpResponse): Unit = {

//    log.info("msg received in http connection handler")
//    System.out.println("CONTENT_TYPE:" + msg.headers().get(HttpHeaders.Names.CONTENT_TYPE));

    this.eventConnectionDelegate.onMessageReceived(msg)
  }

  override def channelActive(ctx: ChannelHandlerContext) = {
    log.debug("channel became active")
    eventConnectionDelegate.connected(ctx)
  }

  override def channelInactive(ctx: ChannelHandlerContext) {
    log.debug("Channel became inactive")
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    // unwrap CodecException if needed
    cause match {
      case _ =>  handleException(cause)
    }

  }

  private def handleException(cause: Throwable) {
    if (!this.connectionPromise.isCompleted) {
      this.connectionPromise.failure(cause)
    }
    eventConnectionDelegate.exceptionCaught(cause)
  }

  override def handlerAdded(ctx: ChannelHandlerContext) {
    this.currentContext = ctx
  }

  def write(req: HttpRequest): ChannelFuture = {
    writeAndHandleError(req)
  }

  private def writeAndHandleError(message: Any): ChannelFuture = {
    if(this.currentContext.channel().isActive) {
      val res = this.currentContext.channel().writeAndFlush(message)
      res.onFailure {
        case e: Throwable => handleException(e)
      }
      res
    } else {
      val error = new Exception("This channel is not active any more")
      handleException(error)
      this.currentContext.channel().newFailedFuture(error)
    }
  }

  def isConnected: Boolean = {
    if (this.currentContext != null && this.currentContext.channel() != null) {
      this.currentContext.channel().isActive
    } else false
  }

  def disconnect: ChannelFuture = this.currentContext.close

}
