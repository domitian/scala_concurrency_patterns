package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging
import akka.pattern._

import akka.util.Timeout
import scala.concurrent._
import scala.util._
import scala.concurrent.ExecutionContext.Implicits.global

class GraceFulPingyActor extends Actor{
    val pongy = context.actorOf(Props[Pongy], "pongy")
    context.watch(pongy)
    /* When we use watch to watch child, it ensures that parent gets
    the terminated message of the child stopped which we can use further */
    def receive = {
        case "Die, Pingy" =>
            context.stop(pongy)
        case Terminated(`pongy`) =>
            context.stop(self)
    }
}

object CommunicatingGraceFulStop extends App{
    val ourSystem = ActorSystem("OurSystem")
    val grace = ourSystem.actorOf(Props[GraceFulPingyActor], "grace")
    /* gracefulStop pattern takes timeout and message to shutdown and 
    returns a future */
    val stopped = gracefulStop(grace, 3.seconds, "Die, Pingy")
    stopped onComplete {
        case Success(x) => 
            log("graceful shutdown succesful")
            ourSystem.terminate()
        case Failure(x) =>
            log("grace not stopped")
            ourSystem.terminate()
    }
}