package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging

class CheckActor extends Actor{
    val log = Logging(context.system,  this)
    def receive = {
        case path: String => 
            log.info(s"Checking path $path")
            context.actorSelection(path) ! Identify(path)
        case ActorIdentity(path, Some(ref)) =>
            log.info(s"found actor $ref at $path")
        case ActorIdentity(path, None) =>
            log.info(s"no actor at $path")
    }
}

object ActorSelection extends App{
    lazy val ourSystem = ActorSystem("OurSystem")
    val checker = ourSystem.actorOf(Props[CheckActor], "checker")
    checker ! "../*"
    checker ! "/joker"
    checker ! "/system/*"
    Thread.sleep(100)
    ourSystem.terminate()
}