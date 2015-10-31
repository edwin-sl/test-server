package controllers

import model.{User, Users}
import play.api.Play
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSResponse, WS}
import play.api.mvc._
import play.api.Play.current
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def testGet = Action{ implicit req => {
    Ok("Fue GET")
  }}

  def testPost = Action{ req => {
    val body = req.body
    println(body)
    Ok("Fue POST -> \n" + body)
  }}

  def sendGCM(msg: JsValue): WSResponse = {
    println("Message -> \n" + Json.prettyPrint(msg))
    val key = Play.current.configuration.getStringList("google.key").get.get(0)
    val send = WS.url("https://gcm-http.googleapis.com/gcm/send")
      .withHeaders(
        ("Content-Type", "application/json"),
        ("Authorization", "key=" + key))
    .post(msg)
    Await.result(send, Duration.Inf)
  }

  def sendNotification(user: String) = Action{ implicit req => {
    val id = Users.getId(user)
    val msg = Json.obj("to" -> id)

    Ok(sendGCM(msg).body)
  }}

  def sendMessage(user: String) = Action{ implicit req => {
    val data = req.body.asJson.getOrElse(Json.obj("status" -> "failed"))
    val id = Users.getId(user)
    val msg = Json.obj(
      "data" -> data,
      "to" -> id
    )

    Ok(sendGCM(msg).body)
  }}

  def broadcastNotification = Action{ implicit req => {
    val ids = Users.getIds
    val msg = Json.obj("registration_ids" -> ids)

    Ok(sendGCM(msg).body)
  }}

  def broadcastMessage = Action{ implicit req => {
    val data = req.body.asJson.getOrElse(Json.obj("status" -> "failed"))
    val ids = Users.getIds
    val msg = Json.obj(
      "data" -> data,
      "registration_ids" -> ids
    )

    Ok(sendGCM(msg).body)
  }}

  def registerAndroid = Action{ implicit req => {
    val user_data: (String, String) = req.method match {
      case "GET" => (req.getQueryString("user").get, req.getQueryString("id").get)
      case "POST" => {
        val body = req.body.asJson.get
        (body.\("user").as[String], body.\("id").as[String])
      }
    }
    val response = if(Users.addUser(User(user_data._1, user_data._2)))
      Json.obj("status" -> "ok")
    else
      Json.obj("status" -> "fail")

    Ok(response)
  }}

  def showIds = Action{
    Ok(Users.getUsers.mkString("\n"))
  }

  def showUsers = Action{
    Ok(Users.getUsers.mkString("\n"))
  }

  def cleanUsers = Action{
    Users.cleanUsers()
    Ok("OK")
  }
}