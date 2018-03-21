package org.learningconcurrency.chapter4
import org.learningconcurrency._


import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io._
import scala.concurrent.duration._

object BlockingAwait extends App{
	val urlSpecSizeFuture = Future {
		val f: Future[Int] = Future{
			val specUrl = "https://www.w3.org/Addressing/URL/url-spec.txt"
			Source.fromURL(specUrl).size
		}
		val specSize = Await.result(f, 10.seconds)
		log(s"size of spec is $specSize")
	}

	// It's not good to block as it leads to thread starvation and waste of resources,
	// so use blocking to signal ExecutionContext that worker is blocked, so allows it to
	// temporarily spawn additional threads
	val startTime = System.nanoTime
	val futures = for (i <- 0 to 31) yield Future{
		blocking{
			Thread.sleep(1000)
			log(s"$i: Running")
		}
	}

	for (f <- futures) Await.result(f, Duration.Inf)
	val endTime = System.nanoTime
	log(s"Total time = ${(endTime - startTime)/1000000} ms")
	log(s"Total CPUs = ${Runtime.getRuntime.availableProcessors}")
}