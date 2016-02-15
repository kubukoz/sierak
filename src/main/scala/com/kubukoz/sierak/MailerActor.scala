package com.kubukoz.sierak

import javax.mail.internet.InternetAddress

import akka.actor.{Actor, ActorLogging}
import com.kubukoz.sierak.MailerActor._
import com.kubukoz.sierak.Tools._
import courier.{Envelope, Mailer, Text}

import scala.concurrent.duration._
import scala.language.implicitConversions
import scala.util.Success

class MailerActor(login: String, password: String, recipients: Seq[String]) extends Actor with ActorLogging {

  import context.dispatcher

  def send: Receive = {
    case Send =>
      log.info("Sending message")
      sendMails()
  }

  override def receive: Receive = {
    case Wait =>
      futureAreResultsAvailable.map {
        case true =>
          log.info("Sending Send signal")
          context.become(send)
          self ! Send
        case _ =>
          log.info("Waiting...")
          context.system.scheduler.scheduleOnce(1.minute, self, Wait)
      }
  }

  def sendMails() = {
    val mailer = Mailer(host = "smtp.gmail.com", port = 587)
      .auth(true)
      .as(s"$login@gmail.com", password)
      .startTtls(true)()

    val envelope = Envelope.from(login at "gmail.com")
      .subject("Wyniki z fizy")
      .content(Text("Są wyniki z fizy. Chyba."))

    mailer(envelope.to(recipients.map(stringToAddress): _*)).onComplete {
      case Success(_) => println("message delivered")
      case _ => println("message NOT delivered")
    }
  }
}

object MailerActor {

  implicit class Addressable(name: String) {
    def at(host: String): InternetAddress = new InternetAddress(s"$name@$host")
  }

  implicit def stringToAddress(s: String): InternetAddress = new InternetAddress(s)

  case object Send

  case object Wait

}
