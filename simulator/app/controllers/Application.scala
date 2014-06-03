package controllers

import play.api._
import play.api.mvc._
import core.simulator

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def test(a: Int) = Action {
    val l = List(a,2,3)
    Ok(l.toString())
  }

/*
  def simRetirement = Action {
    val simulator = new simulator
    val market = simulator.simulateMarketReturns()

    simulator.simulateEarlyRetirement()

  }
*/

}