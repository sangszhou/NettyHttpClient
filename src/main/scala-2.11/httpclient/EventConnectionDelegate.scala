package httpclient

import io.netty.channel.ChannelHandlerContext

/**
  * Created by xinszhou on 6/14/16.
  */
trait EventConnectionDelegate {
  def connected(ctx : ChannelHandlerContext )
  def onMessageReceived(result: String)
  def exceptionCaught( exception : Throwable )
}
