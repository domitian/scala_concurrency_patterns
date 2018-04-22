package org.learningconcurrency.chapter8
import org.learningconcurrency._
import scala.concurrent.duration._
import akka.actor._
import akka.event.Logging
import scala.io._
import org.apache.commons.io.FileUtils
import scala.collection._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._

object DownloadManager{
    case class Download(url: String, dest: String)
    case class Finished(dest: String)
}
class Downloader extends Actor{
    def receive = {
        case DownloadManager.Download(url, dest) =>
            val content = Source.fromURL(url)
            FileUtils.write(new java.io.File(dest), content.mkString)
            sender ! DownloadManager.Finished(dest)
    }
}

class DownloadManager(val downloadSlots: Int) extends Actor{
    import DownloadManager._
    val log = Logging(context.system, this)
    val downloaders = mutable.Queue[ActorRef]()
    val pendingWork = mutable.Queue[Download]()
    val workItems = mutable.Map[ActorRef, Download]()

    private def checkDownloads(): Unit = {
        if (pendingWork.nonEmpty && downloaders.nonEmpty){
            val dl = downloaders.dequeue()
            val item = pendingWork.dequeue()
            log.info(s"download started for $item , ${downloaders.size} download slots left")
            dl ! item
            workItems(dl) = item
        }
    }

    def receive = {
        case msg @ DownloadManager.Download(url, dest) =>
            pendingWork.enqueue(msg)
            checkDownloads()
        case DownloadManager.Finished(dest) =>
            workItems.remove(sender)
            downloaders.enqueue(sender)
            log.info(s"$dest done, ${downloaders.size} download slots left")
            checkDownloads()
    }

    override def preStart(): Unit = {
        for(i <- 0 until downloadSlots){
            val dl = context.actorOf(Props[Downloader], s"dl$i")
            downloaders.enqueue(dl)
        }
    }

    override val supervisorStrategy = 
        OneForOneStrategy(
            maxNrOfRetries = 20, withinTimeRange = 2 seconds
        ){
            case fnf: java.io.FileNotFoundException =>
                log.info(s"Resource not found: $fnf")
                workItems.remove(sender)
                downloaders.enqueue(sender)
                Resume // Ignores the exception and resumes actor
            case _ => Escalate // escalate to parent actor
        }

}

object DownloadManagerTest extends App{
    lazy val ourSystem = ActorSystem("OurSystem")
    val downloadManager = ourSystem.actorOf(Props(classOf[DownloadManager], 4), "man")
    downloadManager ! DownloadManager.Download(
        "https://www.w3.org/Addressing/URL/url-spec.txt",
        "url-spec.txt"
    )
    downloadManager ! DownloadManager.Download(
        "https://www.w3.org/Addressing/URL/invalid-spec.txt",
        "url-spec.txt"
    )
    Thread.sleep(2000)
    ourSystem.terminate()
}