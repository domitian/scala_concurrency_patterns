package org.learningconcurrency.chapter4
import org.learningconcurrency._


import scala.concurrent._
import ExecutionContext.Implicits.global

import java.util._

object FutureTimer extends App{
	private val timer = new Timer(true)

	def timeout(t: Long): Future[Unit] = {
		val p = Promise[Unit]
		timer.schedule(new TimerTask {
			def run() = {
				p success ()
				timer.cancel
			}
		}, t)
		p.future
	}

	timeout(1000) foreach {case _ => log(s"called callback after timeout")}
	Thread.sleep(2000)
}