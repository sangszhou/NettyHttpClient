package common

import scala.concurrent.Future


/**
  * Created by xinszhou on 6/14/16.
  */
trait Connection {
  def disconnect: Future[Connection]
  def connect: Future[Connection]
  def isConnect: Boolean
  def sendQuery(query: String): Future[String]
}

