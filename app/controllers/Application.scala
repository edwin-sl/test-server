package controllers

import controllers.GCMMessage.MessageType
import model.Users
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

  def sendGCM(msg: JsValue): JsValue = {
    println("Message -> \n" + Json.prettyPrint(msg))
    val key = Play.current.configuration.getString("google.key").get
    val send = WS.url("https://gcm-http.googleapis.com/gcm/send")
      .withHeaders(
        ("Content-Type", "application/json"),
        ("Authorization", "key=" + key))
    .post(msg)
    val response = Await.result(send, Duration.Inf)
    println("Response -> " + response.body)
//    response.body
    val jsonResponse = response.json
    Json.obj(
      "success" -> jsonResponse.\("success").as[Int],
      "failure" -> jsonResponse.\("failure").as[Int])
  }

  def sendNotification(user: String) = Action{ implicit req => {
    val id = Users.getId(user)
    val response = if(id.isEmpty) {
      Json.obj("status" -> "error")
    } else {
      val msg = GCMMessage.createMessage(MessageType.Message, id)

      val msgJson = sendGCM(msg)
      Json.obj(
        "status" -> {
          if (msgJson.\("success").as[Int] > 0)
            "ok" else "error"
        },
        "data" -> msgJson
      )
    }
    Ok(response)
  }}

  def sendMessage(user: String) = Action{ implicit req => {
    println(req.body)
    val data = req.body.asJson.getOrElse(Json.obj("status" -> "failed"))
    val id = Users.getId(user)
    val response = if(id.isEmpty) {
      Json.obj("status" -> "error")
    } else {
      val msg = GCMMessage.createMessage(MessageType.Message, id, data)
      val msgJson = sendGCM(msg)
      Json.obj(
        "status" -> {
          if (msgJson.\("success").as[Int] > 0)
            "ok" else "error"
        },
        "data" -> msgJson
      )
    }
    Ok(response)
  }}

  def broadcastNotification = Action{ implicit req => {
    val ids = Users.getIds
    val msg = GCMMessage.createBroadcast(MessageType.Broadcast, ids)
    val msgJson = sendGCM(msg)
    val response = Json.obj(
      "status" -> {
        if(msgJson.\("success").as[Int] > 0)
          "ok" else "error"
      },
      "data" -> msgJson
    )
    Ok(response)
  }}

  def broadcastMessage = Action{ implicit req => {
    println(req.body)
    val data = req.body.asJson.getOrElse(Json.obj())

    val response = if(data.\("message").asOpt[String].isDefined && data.\("from").asOpt[String].isDefined) {
      val ids = Users.getIds
      val msg = GCMMessage.createBroadcast(MessageType.Broadcast, ids, data)

      val msgJson = sendGCM(msg)
      Json.obj(
        "status" -> {
          if (msgJson.\("success").as[Int] > 0)
            "ok"
          else "error"
        },
        "data" -> msgJson
      )
    } else {
      RequestResponse.Error("Invalid Json")
    }
    Ok(response)
  }}

  def registerAndroid = Action{ implicit req => {
    val user_data: (String, String) = req.method match {
      case "GET" => (req.getQueryString("user").getOrElse(""), req.getQueryString("id").getOrElse(""))
      case "POST" => {
        req.body.asJson.fold(("", ""))(body => (body.\("user").asOpt[String].getOrElse(""), body.\("id").asOpt[String].getOrElse("")))
      }
    }

    val response = if(user_data._1.isEmpty || user_data._2.isEmpty)
      Json.obj("status" -> "error")
    else if(Users.addUser(user_data._1, user_data._2)) {
      val msg = GCMMessage.createBroadcast(MessageType.Register, Users.getIds)
      sendGCM(msg)
      Json.obj("status" -> "ok")
    }
    else
      Json.obj("status" -> "error")

    Ok(response)
  }}

  def showIds = Action{
    Ok(Json.obj("status" -> "ok",
      "data" -> Users.getIds))
  }

  def showUsers = Action{
//    Ok(RequestResponse.Success(Users.getUsers))
    Ok(Json.obj("status" -> "ok",
      "data" -> Users.getUsers))
  }

  def cleanUsers = Action{
    Users.cleanUsers()
    Ok(RequestResponse.Success)
  }
}