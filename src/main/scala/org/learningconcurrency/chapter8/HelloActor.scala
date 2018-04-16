package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging

class HelloActor(val hello: String) extends Actor{
    val log = Logging(context.system, this)
    def receive = {
        case `hello` => log.info("Received Hello Message")
        case msg => 
            log.info(s"Unexpected $msg")
            context.stop(self)
    }
}

object HelloActor{
    def props(hello: String) = Props(new HelloActor(hello))
    def propsAlt(hello: String) = Props(classOf[HelloActor], hello)
}

object ActorsCreate extends App{
    lazy val ourSystem = ActorSystem("OurSystem")
    val hiActor: ActorRef = ourSystem.actorOf(HelloActor.props("hi"), name = "greeter")
    hiActor ! "hi"
    hiActor ! "hola"
    Thread.sleep(500)
    ourSystem.terminate()
}