package pool

import common.Connection

import scala.concurrent.Future
import scala.util.Try

/**
  * Created by xinszhou on 6/15/16.
  */
trait ObjectFactory[T] {

  def create: T

  def destroy(item: T)

  def validate(item: T): Try[T]

  def test(item: T): Try[T] = validate(item)
}
