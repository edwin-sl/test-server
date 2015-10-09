package controllers

import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def testGet = Action{ implicit req => {
    Ok("Fue GET")
  }}

  def testPost = Action{ implicit req => {
    val body = req.body
    Ok("Fue POST -> \n" + body)
  }}
}