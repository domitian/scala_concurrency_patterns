package org.learningconcurrency.chapter5
import org.learningconcurrency._

import scala.collection._
import scala.util.Random
import java.util.concurrent.atomic._
/* Will have bad perf with parallel collection if there is memory
contention as like here, all processors share the same uid and since it's
atomic they need to wait for others to finish before accessing it themselves.
this happens at cpu cache level as well. */
object ParUid extends App{
	private val uid = new AtomicLong(0L)
	val seqTime = timed {
		for (i <- (0 until 1000000)) uid.incrementAndGet
	}
	log(s"sequential time $seqTime")
	val parTime = timed {
		for (i <- (0 until 1000000).par) uid.incrementAndGet
	}
	log(s"parallel time $parTime")
}