package model

import com.redis.RedisClient
import play.api.Play

/**
 * Created by edwin on 11/5/2015.
 */
object RedisManager {
  val HOST = Play.current.configuration.getString("redis.heroku.host").get
  val PORT = Play.current.configuration.getInt("redis.heroku.port").get
  val SECRET = Play.current.configuration.getString("redis.heroku.secret")

  val r = new RedisClient(HOST, PORT, 0, SECRET)

  def setValue(key: String, value: String) = {
    r.setnx(key, value)
  }

  def getValue(key: String): String = {
    r.get(key).getOrElse("")
  }

  def getKeys: List[String] = {
    r.keys().fold(List.empty[String])(_.map(_.getOrElse("")))
  }

  def getValues: List[String] = {
    val keys = getKeys
    r.mget(keys.head, keys.tail : _*)
      .fold(List.empty[String])(_.map(_.getOrElse("")))
  }

  def cleanRedis = {
    r.flushall
  }
}
