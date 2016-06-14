package util

import io.netty.channel.{ChannelFuture, ChannelFutureListener}

import scala.concurrent.{Future, Promise}

/**
  * Created by xinszhou on 6/14/16.
  */
object ChannelFutureTransformer {

  implicit def toFuture(channelFuture: ChannelFuture): Future[ChannelFuture] = {
    val promise = Promise[ChannelFuture]

    channelFuture.addListener(new ChannelFutureListener {
      override def operationComplete(future: ChannelFuture): Unit = {
        future.isSuccess match {
          case true => promise.success(future)
          case false =>
            val exception = if (future.cause() == null) {
              new Exception("canceled channel future exception")
            } else future.cause()

            promise.failure(exception)
        }
      }
    })

    promise.future
  }
}
