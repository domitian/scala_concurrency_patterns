package org.learningconcurrency.chapter6
import org.learningconcurrency._
import rx.lang.scala._
import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source

object CompositionRetries extends App{
    import Observable._
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

    def errorMessage = just("Retrying...") ++ error(new Exception)
    def quoteMessage = for{
        text <- fetchQuoteObservable
        message <- if (text.size < 65) just(text) else errorMessage
    } yield message
    quoteMessage.retry(5).subscribe(log _)
    Thread.sleep(2500)
}