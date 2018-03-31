package org.learningconcurrency.chapter6
import org.learningconcurrency._
import rx.lang.scala._
import scala.concurrent.duration._

object ObservableItems extends App{
    /* Here we created a synchronous observable collection */
    val o = Observable.just("Pascal", "Java", "Scala")
    o.subscribe(name => log(s"learned $name language"))
    o.subscribe(name => log(s"forgot $name language"))
}

object ObservablesTimer extends App{
    /* Here we create a asyncrhonous observable whose callbacks are asynchronous */
    val o = Observable.interval(1.second).take(1)
    o.subscribe(_ => log("Timeout!"))
    o.subscribe(_ => log("Another Timeout!"))
    Thread.sleep(1100)
}

object ObservablesExceptions extends App{
    /* Here observable throws an exception which can be handled by having a error
    callback function(overlaoded), once error is encountered observable stops sending events*/
    val exc = new RuntimeException
    val o = Observable.just(1,2) ++ Observable.error(exc)
    o.subscribe(
        x => log(s"number $x"),
        t => log(s"an error occured $t")
    )
}