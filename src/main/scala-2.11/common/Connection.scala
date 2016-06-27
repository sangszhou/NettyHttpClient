package common

import io.netty.handler.codec.http.{HttpRequest, HttpResponse}

import scala.concurrent.Future


/**
  * Created by xinszhou on 6/14/16.
  */
trait Connection {

  def disconnect: Future[Connection]

  def connect: Future[Connection]

  def isConnected: Boolean

  def sendQuery(query: HttpRequest): Future[HttpResponse]
}

