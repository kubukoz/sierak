package com.kubukoz.polskibus.service

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.Config

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps
import scala.sys.process._

trait Service{
  implicit val actorSystem: ActorSystem
  implicit val config: Config

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: ActorMaterializer
  implicit val logger: LoggingAdapter

  implicit val routeActorCallTimeout = Timeout(5 seconds)

  def getResults = Future{
    "curl http://www.if.pw.edu.pl/~sierak/Wyniki_Is_2015-2016.doc" #> "/dev/null" !!
  }.map(_.length > 33787).map{
    case true => "CHYBA SĄ WYNIKI"
    case _ => "NIE MA WYNIKÓW"
  }

  val routes =
    logRequestResult("sierak") {
      get {
        complete {
          getResults
        }
      }
    }
}