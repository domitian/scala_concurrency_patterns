package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.annotation.tailrec

import scala.io.Source
import scala.concurrent._
import ExecutionContext.Implicits.global

object FutureCreate extends App {
	Future {
		log("Just logging this in the future")
	}
	log("Future is coming")
	Thread.sleep(100)
}

// Here are polling and checking whether future has returned or not
// Not always the best way to do this
object FuturesDataType extends App {
	val buildFile: Future[String] = Future {
		val f = Source.fromFile("build.sbt")
		try f.getLines.mkString("\n") finally f.close
	}
	log("Started reading build file asynchronously")
	log(s"Is file reading status: ${buildFile.isCompleted}")
	Thread.sleep(200)
	log(s"status: ${buildFile.isCompleted}")
	log(s"value: ${buildFile.value}")
}