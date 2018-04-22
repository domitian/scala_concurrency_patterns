package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging
import akka.pattern._

class Router extends Actor{
    var i = 0
    val children = for (_ <- 1 to 4) yield
        context.actorOf(Props[StringPrinter])
    /* The reason we use forward pattern instead of just sending
    message normally using ! is because sender reference will remain
    the same to the receiver. */
    def receive = {
        case msg => 
            children(i) forward msg
            i = (i+1) % 4
    }
}

object ForwardPattern extends App{
    val ourSystem = ActorSystem("OurSystem")
    val router = ourSystem.actorOf(Props[Router], "router")
    router ! "hola"
    router ! "hi"
    Thread.sleep(200)
    ourSystem.terminate()
}