package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging
import akka.pattern._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._

class Naughty extends Actor{
    val log = Logging(context.system, this)
    def receive = {
        case s: String => log.info(s)
        case msg => throw new RuntimeException
    }

    override def postRestart(t: Throwable) = 
        log.info("naughty restarted")
}

class SuperVisor extends Actor{
    val child = context.actorOf(Props[Naughty], "naughty")
    def receive = PartialFunction.empty
    override val supervisorStrategy = 
        OneForOneStrategy() {
            case ake: ActorKilledException => Restart
            case _ => Escalate
        }
}

object SuperVisorOneStrategy extends App{
    lazy val ourSystem = ActorSystem("OurSystem")
    ourSystem.actorOf(Props[SuperVisor], "super")
    ourSystem.actorSelection("/user/super/*") ! Kill
    ourSystem.actorSelection("/user/super/*") ! "Sorry about that"
    ourSystem.actorSelection("/user/super/*") ! 1
    Thread.sleep(300)
    ourSystem.terminate()
}