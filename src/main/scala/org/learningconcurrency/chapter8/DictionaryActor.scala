package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging
import scala.collection._
import scala.io._

object DictionaryActor {
    case class Init(path: String)
    case class IsWord(w: String)
    case object End
}

class DictionaryActor extends Actor {
    private val log = Logging(context.system, this)
    private val dictionary = mutable.Set[String]()
    def receive = uninitialized

    def uninitialized: PartialFunction[Any, Unit] = {
        case DictionaryActor.Init(path) => {
            val words = Source.fromFile(path)
            for (w <- words.getLines) dictionary += w
            context.become(initialized)
        }
    }

    def initialized: PartialFunction[Any, Unit] = {
        case DictionaryActor.IsWord(w: String) => 
            log.info(s"word $w exists ${dictionary(w)}")
        case DictionaryActor.End => 
            dictionary.clear()
            context.become(uninitialized)
    }

    override def unhandled(msg: Any) = {
        log.info(s"Can't handle message $msg in this state")
    }
}

object DictionaryActorTest extends App{
    lazy val ourSystem = ActorSystem("OurSystem")
    val dict = ourSystem.actorOf(Props[DictionaryActor], "dictionary")
    dict ! DictionaryActor.IsWord("blabla")
    Thread.sleep(100)
    dict ! DictionaryActor.Init("/usr/share/dict/words")
    dict ! DictionaryActor.IsWord("program")
    Thread.sleep(100)
    dict ! DictionaryActor.IsWord("balaban")
    Thread.sleep(100)
    ourSystem.terminate()
}