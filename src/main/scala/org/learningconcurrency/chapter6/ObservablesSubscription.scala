package org.learningconcurrency.chapter6
import org.learningconcurrency._

import scala.annotation.tailrec

import scala.io.Source
import scala.concurrent._
import ExecutionContext.Implicits.global

import org.apache.commons.io.monitor._
import rx.lang.scala._

/* In the below example, events are emitted only when observables
are subscribed, they are called cold observables. So when they are
subscribed, we have to instantiate a new instance of observable each
time. Hot observables emit events even when not subscribed.
For example like keyboard or mouse observables. So downside is they use
resources even when not subscribed but they have only instance of observable.
even for multiple subscriptions. In the below example if we had 
filemonitor outside the modified method, it would have become
a hot observable and also subscription apply method will have 
a remove listener method instead of stop file monitor method. */
object ObservablesSubscriptions extends App{
    def modified(directory: String): Observable[String] = {
        Observable.create {observer =>
            val fileMonitor = new FileAlterationMonitor(1000)
            val fileObs = new FileAlterationObserver(directory)
            val fileLis = new FileAlterationListenerAdaptor {
                override def onFileChange(file: java.io.File) =  {
                    observer.onNext(file.getName)
                }
            }
            fileObs.addListener(fileLis)
            fileMonitor.addObserver(fileObs)
            fileMonitor.start()
            Subscription{fileMonitor.stop()}
        }
    }
    log("starting to monitor files")
    val sub = modified(".").subscribe(n => log(s"$n modified!"))
    log("please modify and save a file")
    Thread.sleep(10000)
    sub.unsubscribe()
    log("Monitoring done!")
}

/* This is hot observable implementation  */
object HotObservablesSubscriptions extends App{
    val fileMonitor = new FileAlterationMonitor(1000)
    fileMonitor.start()
    def modified(directory: String): Observable[String] = {
        val fileObs = new FileAlterationObserver(directory)
        fileMonitor.addObserver(fileObs)
        Observable.create {observer =>
            val fileLis = new FileAlterationListenerAdaptor {
                override def onFileChange(file: java.io.File) =  {
                    observer.onNext(file.getName)
                }
            }
            fileObs.addListener(fileLis)
            Subscription{fileObs.removeListener(fileLis)}
        }
    }
    log("starting to monitor files")
    val sub = modified(".").subscribe(n => log(s"$n modified!"))
    log("please modify and save a file")
    Thread.sleep(10000)
    sub.unsubscribe()
    fileMonitor.stop()
    log("Monitoring done!")
}