package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source

object FuturesFailure extends App{
	def getUrlSpec(): Future[List[String]] = Future {
		val invalidUrl = "https://www.w3.org/Addressing/URL/invalid-url-spec.txt"
		val f = Source.fromURL(invalidUrl)
		try f.getLines.toList finally f.close
	}
	val urlSpec: Future[List[String]] = getUrlSpec
	urlSpec.failed foreach {
		case t => log(s"Exception occurrend - $t")
	}
	Thread.sleep(1000)
}