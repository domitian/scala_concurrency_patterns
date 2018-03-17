package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source

object PromisesCreate extends App {
	val p = Promise[String]
	val q = Promise[String]
	p.future foreach log
	Thread.sleep(100)
	p success "assigned"

	q failure new Exception("not kept")
	q.future.failed foreach {case t => log(s" q promise failed with $t")}
}