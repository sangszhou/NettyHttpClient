package common


import scala.concurrent.duration._

/**
  * Created by xinszhou on 6/14/16.
  */

case class Configuration(
                          host: String,
                          port: Int,
                          connectTimeout: Duration = 5 second,
                          queryTimeout: Option[Duration] = None,
                          maximumMessageSize: Int = 16777216
                        )



object PoolConfiguration {
  val Default = new PoolConfiguration(100, 10, 300)
}


/**
  *
  * Defines specific pieces of a pool's behavior.
  *
  * @param maxObjects how many objects this pool will hold
  * @param maxIdle number of milliseconds for which the objects are going to be kept as idle (not in use by clients of the pool)
  * @param maxQueueSize when there are no more objects, the pool can queue up requests to serve later then there
  *                     are objects available, this is the maximum number of enqueued requests
  * @param validationInterval pools will use this value as the timer period to validate idle objects.
  */
case class PoolConfiguration(
                              maxObjects: Int,
                              maxIdle: Long,
                              maxQueueSize: Int,
                              validationInterval: Long = 5000
                            )
