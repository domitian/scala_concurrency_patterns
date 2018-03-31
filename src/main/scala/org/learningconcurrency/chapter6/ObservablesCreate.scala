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