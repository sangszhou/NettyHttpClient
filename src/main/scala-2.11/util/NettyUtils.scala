package util

import io.netty.channel.nio.NioEventLoopGroup
import io.netty.util.internal.logging.{InternalLoggerFactory, Slf4JLoggerFactory}

/**
  * Created by xinszhou on 6/14/16.
  */
object NettyUtils {
  InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory())
  lazy val DefaultEventLoopGroup = new NioEventLoopGroup(0, DaemonThreadFactory("httpclient-netty"))
}
