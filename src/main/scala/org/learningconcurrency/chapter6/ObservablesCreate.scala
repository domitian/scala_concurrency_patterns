package org.learningconcurrency.chapter6
import org.learningconcurrency._
import rx.lang.scala._
import scala.concurrent.duration._

/* The subscribe method here is synchronous, but we can also create
an asynchronous method as */
object ObservablesCreate extends App{
    val vms = Observable.create[String] {obs =>
        obs.onNext("Dart")
        obs.onNext("JVM")
        obs.onNext("V8")
        obs.onCompleted()
        Subscription()
    }
    vms.subscribe(log _, e => log(s"oops - $e"), () => log("Done!"))
}
import scala.concurrent._
import ExecutionContext.Implicits.global
object ObservablesCreateFuture extends App{
    /* Use create method for callback api to create asynchronous
    observables and from method for everything else. */
    val f = Future {"Back to the Future(s)"}
    val o = Observable.create[String] { obs =>
        f.foreach {case s => obs.onNext(s); obs.onCompleted()}
        f.failed foreach {case t => obs.onError(t)}
        Subscription()
    }
    o.subscribe(log _)

    val ao = Observable.from(Future {"Back to the Future(s)"})
    ao.subscribe(log _)
    Thread.sleep(200)
}