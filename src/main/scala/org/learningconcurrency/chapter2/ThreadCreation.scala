package org.learningconcurrency

import org.learningconcurrency._

object ThreadCreation extends App{
	class MyThread extends Thread{
		override def run(): Unit = {
			println("New thread running")
		}
	}
	val t = new MyThread
	t.start()
	t.join()
	println("New thread joined")

}