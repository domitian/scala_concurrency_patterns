package org


package object learningconcurrency {
	def log(msg: String): Unit = {
		println(s"${Thread.currentThread.getName}: $msg")
	}
	def thread(body:  => Unit): Thread = {
		val t = new Thread {
			override def run() = body
		}
		t.start()
		t
	}

	@volatile var dummy: Any = _

	def timed[T](body: =>T): Double = {
		val start = System.nanoTime
		dummy = body
		val end = System.nanoTime
		((end - start)/1000)/1000.0
	}
}