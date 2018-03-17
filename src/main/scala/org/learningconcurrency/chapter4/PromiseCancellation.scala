package org.learningconcurrency.chapter4
import org.learningconcurrency._


import scala.concurrent._
import ExecutionContext.Implicits.global

object PromiseCancellation extends App {
	type Cancellable[T] = (Promise[Unit], Future[T])

	def cancellable[T](b: Future[Unit] =>T): Cancellable[T] = {
		val cancel = Promise[Unit]

		val f = Future{
			var r = b(cancel.future)
			if (!cancel.tryFailure(new Exception)){
				throw new CancellationException
			}
			r
		}
		(cancel, f)
	}

	// async computation which takes cancel promise
	val (cancel, value) = cancellable {cancel => 
		var i = 0
		while (i < 5){
			if (cancel.isCompleted) throw new CancellationException
			Thread.sleep(500)
			log(s"$i: working")
			i += 1
		}
		"resulting value"
	}
	Thread.sleep(1500)
	cancel success ()
	log("computation cancelled")
	Thread.sleep(2000)
}