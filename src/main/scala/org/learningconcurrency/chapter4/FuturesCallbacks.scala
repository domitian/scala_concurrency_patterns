package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source

object FuturesCallbacks extends App{

	def getUrlSpec(): Future[List[String]] = Future {
		val url = "https://www.w3.org/Addressing/URL/url-spec.txt"
		val f = Source.fromURL(url)
		try f.getLines.toList finally f.close
	}
	val urlSpec: Future[List[String]] = getUrlSpec

	def find(lines: List[String], keyword: String): String = {
		lines.zipWithIndex collect {
			case (line, n) if line.contains(keyword) => (n, line)
		} mkString("\n")
	}

	// Callback foreach similar to onsuccess
	/* Callback is only called after the future is completed and also it's not 
	guaranteed to immediately call the callback function after future completes, it just
	schedules another task to run the callback. So execution context takes care of running it instead*/
	urlSpec foreach {
		case lines => log(find(lines, "telnet"))
	}
	urlSpec foreach {
		case lines => log(find(lines, "password"))
	}
	log("Callback registered, doing other work now")
	Thread.sleep(2000)
}