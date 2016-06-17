package pool

/**
  * Created by xinszhou on 6/16/16.
  */
trait ObjectPoolControl[T] {

  def setMaxTotal(max: Int)

  def getMaxTotal: Int

  def setDefaultMaxPerRoute(max: Int)

  def getDefaultMaxPerRoute: Int

  def setMaxPerRoute(route: T, max: Int)

  def getMaxPerRoute(route: T): Int

}
