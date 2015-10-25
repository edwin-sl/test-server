package controllers

import model.Users
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
    val send = WS.url("https://gcm-http.googleapis.com/gcm/send")
      .withHeaders(
        ("Content-Type", "application/json"),
        ("Authorization", "key=AIzaSyDF1bcfh7XGvyeD7RKVsMNW5nG523NR__M"))
    .post(msg)
    Await.result(send, Duration.Inf)
  }

  def sendNotification(user: String) = Action{ implicit req => {
    val id = Users.getId(user)
    val msg = Json.obj("to" -> id)

    Ok(sendGCM(msg).body)
  }}

  def sendMessage(user: String) = Action{ implicit req => {
    val data = req.body.asJson.getOrElse(Json.obj())
    val id = Users.getId(user)
    val msg = Json.obj(
      "data" -> data,
      "to" -> id
    )

    Ok(sendGCM(msg).body)
  }

  }

  def registerAndroid = Action{ implicit req => {
    req.method match {
      case "GET" => (req.getQueryString("user").get, req.getQueryString("id").get)
      case "POST" => {
        val body = req.body.asJson.get
        (body.\("user"), body.\("id"))
      }
    }

    Ok(req.method)
  }}
}