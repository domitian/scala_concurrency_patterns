package org.learningconcurrency.chapter3

import org.learningconcurrency._
import scala.annotation.tailrec
import scala.concurrent._
import java.util.concurrent.atomic._
import java.lang._

object Exercise3 extends App {
	// 1
	class PiggyBackContext extends ExecutionContext {

		def execute(runnable: Runnable): Unit = runnable.run()

		def reportFailure(cause: Throwable): Unit = println(s"Exception occurred ${cause.printStackTrace}")
	}

	val executor = new PiggyBackContext
	executor.execute(new Runnable{
		override def run(): Unit = log("running this")
	})
	// Below code is for testing reportFailure method of executor
	// executor.execute(new Runnable{
	// 	override def run(): Unit = throw new Exception("awesome")
	// })

	// 2
	sealed class TreiberStack[T]{
		private val stack = new AtomicReference[List[T]](Nil)
		@tailrec def push(x: T): Unit = {
			val xs = stack.get
			val nxs = x :: xs
			if (!stack.compareAndSet(xs, nxs)) push(x)
		}
		@tailrec def pop(): T = {
			val xs = stack.get
			xs match {
				case x :: nxs => if (stack.compareAndSet(xs, nxs)) x else pop()
				case _ => throw new Exception("Stack is empty")
			}
		}
	}
	val st = new TreiberStack[Int]
	execute({for(i <- 1 to 10) st.push(i) })
	for(i <- 10 to 20) execute(st.push(i))
	for (i <- 1 to 10) execute(println(st.pop()))
	Thread.sleep(300)

	// 3 4
	println("question 3 and 4")
	class ConcurrentSortedList[T](implicit val ord: Ordering[T]){
		private val sortedList = new AtomicReference[List[T]](Nil)

		def add(x: T): Unit = {
			val xs = sortedList.get
			val nxs = (x :: xs).sorted
			if (!sortedList.compareAndSet(xs, nxs)) add(x)
		}
		def iterator: Iterator[T] = sortedList.get.iterator
	}

	val csortList = new ConcurrentSortedList[Int]()
	for(i <- 1 to 10) execute(csortList.add(i))
	// Thread.sleep(1)
	val it = csortList.iterator
	Thread.sleep(10)
	while(it.hasNext)
		println(it.next())

	// 5
	class LazyCell[T](initialization: =>T) {
		@volatile private var _bitmap = false
		private var _obj: T = _
		def apply(): T = if (_bitmap) _obj else this.synchronized{
			if (!_bitmap) {
				_bitmap = true
				_obj = initialization
				_obj
			}
			else _obj
		}
	}
	var a = 1
	val c = new LazyCell[Int]({
		a = a + 1
		a
	})
	println(c.apply)
	println(c.apply)

	// 6
	class PureLazyCell[T](initialization: =>T){
		private val _bitmap = new AtomicBoolean(false)
		private var _obj: T = _
		def apply(): T = {
			val doIt = _bitmap.get
			if (doIt) _obj else{
				if (_bitmap.compareAndSet(doIt, true)) {
					_obj = initialization
					_obj
				} else apply
			}
		}
	}
	val d = new PureLazyCell[Int]({
		a = a + 1
		a
	})
	execute(println(d.apply))
	execute(println(d.apply))

	// 7
	class SyncConcurrentMap extends Map {

	}

	// 8
	import scala.sys.process._
	def spawn[T](block: =>T): T = ???
}
