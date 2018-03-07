package org.learningconcurrency

import org.learningconcurrency._
import scala.collection._

object SynchronizedPool extends App{
	private val tasks = mutable.Queue[()=> Unit]()

	val worker = new Thread{
		def poll(): Option[()=> Unit] = tasks.synchronized{
			while (tasks.isEmpty) tasks.wait()
			Some(tasks.dequeue())
		}

		override def run() = {
			while(true) poll() match{
				case Some(task) => task()
				case None => 
			}
		}
	}
	worker.setName("Worker")
	worker.setDaemon(true)
	worker.start()

	def asynchronous(body: => Unit) = tasks.synchronized{
		tasks.enqueue(() => body)
		tasks.notify()
	}
	asynchronous({log("Heloo")})
	asynchronous({log("world")})
	Thread.sleep(5000)
}
