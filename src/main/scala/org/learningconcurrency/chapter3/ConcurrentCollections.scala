package org.learningconcurrency.chapter3

import scala.annotation.tailrec

import java.util.concurrent.atomic._
import org.learningconcurrency._
import scala.collection._

// Causes data corrpution because 2 threads modify collection at same time
object CollectionBad extends App{
	val buffer = mutable.ArrayBuffer[Int]()
	def asynchAdd(numbers: Seq[Int]) = execute {
		buffer ++= numbers
		log(s"buffer = $buffer")
	}
	asynchAdd(1 until 10)
	asynchAdd(10 until 20)
	Thread.sleep(500)
}

// concurrent collections are far more performant than manually built concurrent like the below one.
// But all concurrent collections face issues when too many threads try to modify them, performance degrades
// so they have scalability issues, so atleast use builtin concurrent collections to get decent perf
class AtomicBuffer[T] {
	private val buffer = new AtomicReference[List[T]](Nil)
	@tailrec private def +=(x: T): Unit = {
		val xs = buffer.get
		val nxs = x :: xs
		if (!buffer.compareAndSet(xs, nxs)) this += x
	}
}