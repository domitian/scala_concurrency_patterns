package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging
import scala.io._

import com.typesafe.config._

object RemoteSystem{
    def remotingConfig(port: Int) = ConfigFactory.parseString(s"""
        akka{
            actor.provider = "akka.remote.RemoteActorRefProvider"
            remote{
                enabled-transports = ["akka.remote.netty.tcp"]
                netty.tcp {
                    hostname = "127.0.0.1"
                    port = $port
                }
            }
        }
    """)

    def remotingSystem(name: String, port: Int): ActorSystem = {
        ActorSystem(name, remotingConfig(port))
    }
}
object RemotingPongySystem extends App{

    import RemoteSystem._
    val system = remotingSystem("PongyDimension", 24321)
    val pongy = system.actorOf(Props[Pongy], "pongy")
    Thread.sleep(15000)
    system.terminate()
}