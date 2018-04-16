package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging

class DeafActor extends Actor {
    val log = Logging(context.system, this)
    def receive = PartialFunction.empty
    override def unhandled(msg: Any) = msg match{
        case msg: String => log.info(s"I donot hear $msg")
        case msg => super.unhandled(msg)
    }
}


object ActorsUnhandled extends App{
    lazy val ourSystem = ActorSystem("OurSystem")
    val deafActor: ActorRef = ourSystem.actorOf(Props[DeafActor], name="deafy")
    deafActor ! "hi"
    Thread.sleep(200)
    deafActor ! 100
    Thread.sleep(200)
    ourSystem.terminate()
}