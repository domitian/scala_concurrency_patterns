package org.learningconcurrency.chapter6
import org.learningconcurrency._
import rx.lang.scala._
import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source

object CompositionConcatAndFlatten extends App{
    def fetchQuote(num: Long): Future[String] = Future {
        blocking {
            val url = s"http://numbersapi.com/$num"
            Source.fromURL(url).getLines.mkString
        }
    }

    def fetchQuoteObservable(): Observable[String] = {
        val r = scala.util.Random
        Observable.from(fetchQuote(r.nextInt(100)))
    }

    def quotes(): Observable[Observable[String]] = {
        Observable.interval(0.5 seconds).take(4).map{
            n => fetchQuoteObservable().map(txt => s"$n) $txt")
        }
    }
    /* Use concat for maintaining order of map and flatten for everything else
    */
    log("Using concat")
    quotes.concat.subscribe(log _)
    Thread.sleep(4000)
    log("Using flatten")
    quotes.flatten.subscribe(log _)
    Thread.sleep(4000)
    log("Using for") // Same as using flatten or flatMap
    val qs = for{
        i <- Observable.interval(0.5 seconds).take(4)
        q <- fetchQuoteObservable()
    } yield s"$i) $q"
    qs.subscribe(log _)
    Thread.sleep(4000)
}