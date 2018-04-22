package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging
import akka.pattern._

import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global

class Pongy extends Actor{
    val log = Logging(context.system, this)
    def receive = {
        case "ping" =>
            log.info("Got ping message -- ponging back")
            Thread.sleep(2000)
            sender ! "pong"
            log.info("Sent the pong message back finally")
            context.stop(self)
    }

    override def postStop() = log.info("Stopping pongy - going down")
}

class Pingy extends Actor{
    val log = Logging(context.system, this)
    def receive = {
        case pongyRef: ActorRef =>
            implicit val timeout = Timeout(2.seconds)
            val f = pongyRef ? "ping"
            f pipeTo sender
    }
}
object ActSystem{
    val ourSystem = ActorSystem("OurSystem")
}
class Master extends Actor{
    import ActSystem._
    val pingy = ourSystem.actorOf(Props[Pingy], "pingy")
    val pongy = ourSystem.actorOf(Props[Pongy], "pongy")
    def receive = {
        case "start" => pingy ! pongy
        case "pong" => context.stop(self)
    }
}

object AskPattern extends App{
    import ActSystem._
    val masta = ourSystem.actorOf(Props[Master], "masta")
    masta ! "start"
    Thread.sleep(1000)
    ourSystem.terminate()
}



