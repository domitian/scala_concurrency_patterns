package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging

class ChildActor extends Actor{
    val log = Logging(context.system, this)
    def receive = {
        case "sayhi" =>
            val parent = context.parent
            log.info(s"my parent $parent made me say this")
    }
    override def postStop() = log.info("Child actor stopped")
}

class ParentActor extends Actor{
    val log = Logging(context.system, this)
    def receive = {
        case "create" => 
            context.actorOf(Props[ChildActor])
            log.info(s"created a kid; children: ${context.children}")
        case "sayhi" =>
            log.info("Kids say hi")
            for (c <- context.children) c ! "sayhi"
        case "stop" =>
            log.info("parent stopping")
            context.stop(self)
    }
}

object ActorsHierarchy extends App{
    lazy val ourSystem = ActorSystem("OurSystem")
    val parent = ourSystem.actorOf(Props[ParentActor], "parent")
    parent ! "create"
    parent ! "create"
    Thread.sleep(100)
    parent ! "sayhi"
    Thread.sleep(200)
    parent ! "stop"
    Thread.sleep(100)
    ourSystem.terminate()
}