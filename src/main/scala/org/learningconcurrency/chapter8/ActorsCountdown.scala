package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging

class CountDownActor extends Actor{
    val log = Logging(context.system, this)
    var n = 10
    def counting: Actor.Receive = {
        case "count" => {
            n -= 1
            log.info(s"n = $n")
            if (n==0) context.become(done)
        }
    }

    def done = PartialFunction.empty
    def receive = counting
}

object ActorsCountdown extends App{
    lazy val ourSystem = ActorSystem("OurSystem")
    val countActor = ourSystem.actorOf(Props[CountDownActor], name="countActor")
    for (i <- 1 to 20) countActor ! "count"
    Thread.sleep(500)
    ourSystem.terminate()
}