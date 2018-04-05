package org.learningconcurrency.chapter6
import org.learningconcurrency._
import rx.lang.scala._
import scala.concurrent.duration._


/* There is an observable contract where if there are events it will
call onNext method of it's observers and if there are no more events or
errors it can  call onCompleted or onError respectively. But it is not
guaranteed to do so. Some do, some doesn't. So an observable an be 
uncompleted, error or completed states.*/
object ObservablesLifetime extends App{
    val classics = List("Godfather", "Lord of the rings", "Dial M for Murder")
    val movies = Observable.from(classics)
    movies.subscribe(new Observer[String]{
        override def onNext(m: String) = log(s"Movies watchlist - $m")
        override def onError(e: Throwable) = log(s"oops - $e")
        override def onCompleted() = log(s"No more movies")
    })
}