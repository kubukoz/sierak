package com.kubukoz.sierak

import javax.mail.internet.InternetAddress

import akka.actor.{Actor, ActorLogging}
import com.kubukoz.sierak.MailerActor._
import com.kubukoz.sierak.Tools._
import courier.{Envelope, Mailer, Text}

import scala.concurrent.duration._

class MailerActor(login: String, password: String) extends Actor with ActorLogging {

  import context.dispatcher

  def send: Receive = {
    case Send =>
      log.info("Sending message")
      sendMail()
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

  def sendMail() = {
    val mailer = Mailer("smtp.gmail.com", 587)
      .auth(true)
      .as(s"$login@gmail.com", password)
      .startTtls(true)()

    mailer(Envelope.from(login at "gmail.com")
      .to(login at "gmail.com")
      .subject("Wyniki z fizy")
      .content(Text("Są wyniki z fizy. Chyba."))).onSuccess {
      case _ => println("message delivered")
    }
  }
}

object MailerActor {
  implicit class Addressable(name: String) {
    def at(host: String) = new InternetAddress("%s@%s" format(name, host))
  }

  case object Send

  case object Wait

}
