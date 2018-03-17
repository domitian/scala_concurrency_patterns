package org.learningconcurrency.chapter4
import org.learningconcurrency._


import scala.concurrent._
import ExecutionContext.Implicits.global

object MyImplicits {
	implicit class FutureOps[T](val self: Future[T]){
		def or(that: Future[T]): Future[T] = {
			val p = Promise[T]
			self onComplete { case x => p tryComplete x}
			that onComplete {case y => p tryComplete y}
			p.future
		}
	}
}

object FutureApi extends App{
	import MyImplicits._
	val k = Future {"dddd"} or Future{"aaaa"}
	k foreach {case k => println(k)} // Will print whichever future returns value first
}