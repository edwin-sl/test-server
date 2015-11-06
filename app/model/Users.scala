package model

/**
 * Created by edwin on 10/24/2015.
 */
//case class User(user: String, id: String)
object Users {

  def addUser(user: String, id: String): Boolean = {
    RedisManager.setValue(user, id)
  }

  def getId(user: String): String = {
    RedisManager.getValue(user)
  }

  def getIds: List[String] = {
    RedisManager.getValues
  }

  def getUsers: List[String] = {
    RedisManager.getKeys
  }

  def cleanUsers(): Unit = {
    RedisManager.cleanRedis
  }
}
