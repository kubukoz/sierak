package com.kubukoz.sierak

import akka.actor.{ActorSystem, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.kubukoz.sierak.MailerActor.Wait
import com.kubukoz.sierak.Tools._
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.language.postfixOps

object Tools {
  def futureAreResultsAvailable(implicit ec: ExecutionContext) = Future {
    Source.fromURL("http://www.if.pw.edu.pl/~sierak/Wyniki_Is_2015-2016.doc", "iso-8859-1")
  }.map(_.length > 33792)
}

object Main {
  implicit val actorSystem = ActorSystem("sierak")
  implicit val executor = actorSystem.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val routeActorCallTimeout = Timeout(5 seconds)

  val config = ConfigFactory.load()
  val logger = Logging(actorSystem, getClass)

  val routes: Route =
    logRequestResult("sierak") {
      get {
        complete {
          futureAreResultsAvailable.map {
            case true => "CHYBA SĄ WYNIKI"
            case _ => "NIE MA WYNIKÓW"
          }
        }
      }
    }

  val mailer = actorSystem.actorOf(Props(classOf[MailerActor], config.getString("mail.login"), config.getString("mail.password")))

  def main(args: Array[String]): Unit = {
    mailer ! Wait
    Http(actorSystem).bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
  }
}