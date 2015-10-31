package model

/**
 * Created by edwin on 10/24/2015.
 */
case class User(user: String, id: String)
object Users {
  var users = List.empty[User]

  def addUser(user: User) = {
    users = users.::(user)
  }

  def getId(user: String): String = {
    users.find(_.user == user).fold("")(_.id)
  }

  def getIds: List[String] = {
    users.map(_.id)
  }

  def getUsers: List[String] = {
    users.map(_.user)
  }

  def cleanUsers(): Unit = {
    users = List.empty[User]
  }
}
