package com.kubukoz.polskibus.service

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.Config

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.Source
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
    Source.fromURL("http://www.if.pw.edu.pl/~sierak/Wyniki_Is_2015-2016.doc", "iso-8859-1")
  }.map(_.length > 33792).map{
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