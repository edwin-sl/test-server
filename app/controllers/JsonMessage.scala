package controllers

import controllers.GCMMessage.MessageType.MessageType
import org.h2.value.Value
import org.joda.time.Instant
import play.api.libs.json.{JsObject, JsValue, Json}

/**
 * Created by edwin on 11/6/2015.
 */
object RequestResponse {

  def Success(implicit data: JsValue): JsValue = {
    Json.obj("status" -> "ok")
    .++(data.as[JsObject])
  }

  def Error(implicit data: JsValue): JsValue = {
    Json.obj("status" -> "error")
      .++(data.as[JsObject])
  }
}

object GCMMessage {
  object MessageType extends Enumeration {
    type MessageType = Value
    val Message, Register, Broadcast = Value
  }

  def createMessage(t: MessageType, to: String, data: JsValue = Json.obj()): JsValue = {
    Json.obj(
      "data" -> Json.obj("type" -> t.toString)
       .++(data.as[JsObject])
       .++(Json.obj("timestamp" -> (Instant.now().getMillis / 1000).toInt)),
      "to" -> to
    )
  }

  def createBroadcast(t: MessageType, to: List[String], data: JsValue = Json.obj()): JsValue = {
    Json.obj(
      "data" -> Json.obj("type" -> t.toString)
        .++(data.as[JsObject])
        .++(Json.obj("timestamp" -> (Instant.now().getMillis / 1000).toInt)),
      "registration_ids" -> to
    )
  }
}