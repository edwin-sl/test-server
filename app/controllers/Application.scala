package controllers

import play.api.libs.json.Json
import play.api.libs.ws.WS
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

  def sendGCM = Action{ implicit req => {
    val msg = Json.obj("data" -> Json.obj(
      "score" -> "5x1",
      "time" -> "15:10"),
    "to" -> "cQlhpkpFEq4:APA91bGt0eocVRSepSP0TukasdVqsXokrRHi3x65-OBNehy_sSdmztbtBj6Hit4Y21094XeLHGCVY36beHjnKgxjrCU835Tz3YYpBEWqbly4RPENfw0m4wR0BxlNgv1Ghu-DjNHBVDSD")

    val send = WS.url("https://gcm-http.googleapis.com/gcm/send")
      .withHeaders(
        ("Content-Type", "application/json"),
        ("Authorization", "key=AIzaSyDF1bcfh7XGvyeD7RKVsMNW5nG523NR__M"))
    .post(msg)
    val res = Await.result(send, Duration.Inf)
    Ok(res.body)
  }

  }
}