package com.kubukoz.sierak

import java.text.SimpleDateFormat

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
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

object Tools {
  val lastModified = 1455296440000L

  def futureAreResultsAvailable(implicit ec: ExecutionContext) =
    NingWSClient().url("http://www.if.pw.edu.pl/~sierak/Wyniki_Is_2015-2016.doc").get().map { result =>
      val dateString = result.header("Last-Modified")
      val dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z")

      val date = dateString.map(dateFormat.parse)

      date.exists(_.getTime > lastModified)
    }
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


  val login = config.getString("mail.login")
  val password = config.getString("mail.password")
  val recipients = config.getString("mail.recipients").split(",").map(_.trim).toList

  val mailer = actorSystem.actorOf(Props(classOf[MailerActor], login, password, recipients))

  def main(args: Array[String]): Unit = {
    mailer ! Wait
    Http(actorSystem).bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
  }
}