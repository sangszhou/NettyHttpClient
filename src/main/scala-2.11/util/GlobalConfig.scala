package util

import com.typesafe.config.ConfigFactory
import common.Configuration

/**
  * Created by xinszhou on 16/6/14.
  */
object GlobalConfig {
  def config = ConfigFactory.load
  val esServer = Configuration(host = config.getString("es.host"), port = config.getInt("es.port"))

}
