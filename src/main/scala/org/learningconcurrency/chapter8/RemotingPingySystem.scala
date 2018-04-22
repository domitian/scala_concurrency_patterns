package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging
import scala.io._

import com.typesafe.config._
import RemoteSystem._

class Runner extends Actor{
    val log = Logging(context.system, this)
    val pingy = context.actorOf(Props[Pingy], "pingy")
    def receive = {
        case "start" =>
            val pongySys = "akka.tcp://PongyDimension@127.0.0.1:24321"
            val pongyPath = "/user/pongy"
            val url = pongySys + pongyPath
            val selection = context.actorSelection(url)
            selection ! Identify(0)
        case ActorIdentity(0, Some(ref)) =>
            pingy ! ref
        case ActorIdentity(0, None) =>
            log.info("Something's wrong ain't no pongy anywhere")
            context.stop(self)
        case "pong" =>
            log.info("got a pong from another dimension")
            context.stop(self)
    }
}

object RemotingPingySystem extends App{
    val system =remotingSystem("PingyDimension", 24567)
    val runner = system.actorOf(Props[Runner], "runner")
    runner ! "start"
    Thread.sleep(5000)
    system.terminate()
}