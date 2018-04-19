package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging

class StringPrinter extends Actor{
    val log = Logging(context.system, this)
    def receive = {
        case msg => log.info(s"printer go message $msg")
    }

    override def preStart(): Unit = log.info("Printer preStart")
    override def postStop(): Unit = log.info("Printer postStop")    
}

class LifeCycleActor extends Actor{
    val log = Logging(context.system, this)
    var child: ActorRef = _
    def receive = {
        case num: Double => log.info(s"Received a double - $num")
        case num: Int => log.info(s"Received a int - $num")
        case list: List[_] => log.info(s"Received a list - ${list.head}")
        case msg: String => child ! msg
    }

    override def preStart(): Unit = {
        log.info("about to start")
        child = context.actorOf(Props[StringPrinter], "kiddo")
    }

    override def preRestart(t: Throwable, msg: Option[Any]): Unit = {
        log.info(s"about to restart because of $t, during message $msg")
        super.preRestart(t, msg)
    }

    override def postRestart(t: Throwable): Unit = {
        log.info(s"just restarted due to $t")
        super.postRestart(t)
    }

    override def postStop() = log.info("just stopped")
}

object ActorLifeCycle extends App{
    lazy val ourSystem = ActorSystem("OurSystem")
    val testy = ourSystem.actorOf(Props[LifeCycleActor], "testy")
    testy ! math.Pi
    testy ! "hi there"
    Thread.sleep(100)
    testy ! Nil
    Thread.sleep(100)
    ourSystem.terminate()
}