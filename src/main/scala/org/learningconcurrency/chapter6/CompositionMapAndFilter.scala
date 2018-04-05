package org.learningconcurrency.chapter6
import org.learningconcurrency._
import rx.lang.scala._
import scala.concurrent.duration._

object CompositionMapAndFilter extends App{
    val odds = Observable.interval(0.5.seconds)
                .filter(_ % 2 == 1).map(n => s"num $n").take(5)
    odds.subscribe(
        log _, e => log(s"unexpected $e"), () => log("no more odds")
    )
    Thread.sleep(5000)
    val evens = for(i <- Observable.from(0 until 9); if i % 2 == 0) yield s"even $i"
    evens.subscribe(log _)
}