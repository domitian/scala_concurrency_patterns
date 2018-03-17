package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source
import scala.util.control.NonFatal

object PromisesCustomAsync extends App{

	def myFuture[T](b: =>T): Future[T] = {
		val p = Promise[T]
		global.execute(new Runnable{
			def run() = try{
				p success b
			} catch {
				case NonFatal(e) => p failure e
			}
		})
		p.future
	}
	val f = myFuture {"ddd" + "doing something " + "yadayad"}
	f foreach log
}