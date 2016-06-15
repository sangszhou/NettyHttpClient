package httpclient

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpResponse

/**
  * Created by xinszhou on 6/14/16.
  *
  * defines callback function
  */
trait EventConnectionDelegate {

  def connected(ctx: ChannelHandlerContext)

  def onMessageReceived(result: HttpResponse)

  def exceptionCaught(exception: Throwable)
}
