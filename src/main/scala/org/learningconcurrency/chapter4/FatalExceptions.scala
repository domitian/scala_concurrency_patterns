package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.concurrent._
import ExecutionContext.Implicits.global


/* Fatal exceptions are not caught by pattern matching or foreach of futures.
they are directly reported to executioncontext reportfailure method
only non fatal exceptions can be pattern matched */
object FuturesNonFatal extends App{
	val f = Future {throw new InterruptedException}
	val g = Future {throw new IllegalArgumentException}
	f.failed foreach { case t => log(s"error - $t")}
	g.failed foreach { case t => log(s"error - $t")}
}