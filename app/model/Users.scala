package model

/**
 * Created by edwin on 10/24/2015.
 */
case class User(user: String, id: String)
object Users {
  val users = List.empty[User]

  def addUser(user: User) = {
    users.+:(user)
  }

  def getId(user: String): String = {
    users.find(_.user == user).fold("")(_.id)
  }

  def getUsers: List[String] = {
    users.map(_.user)
  }
}
