package testUtil

import io.netty.handler.codec.http._
import util.GlobalConfig

/**
  * Created by xinszhou on 16/6/14.
  */
object HttpRequestFactory {

  def getLocalESMeta(): FullHttpRequest = {
    val request: FullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/")

    request.headers.set(HttpHeaderNames.HOST, GlobalConfig.esServer.host)
    request.headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
    request.headers.add(HttpHeaderNames.CONTENT_TYPE, "application/json")

    return request
  }
}
