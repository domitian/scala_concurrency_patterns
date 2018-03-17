package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source

/**
We can recover from future failure using recover combinator, it takes the throwable
object and returns another future. As long as new future is succesful, there won't be
any errors in the future.
*/

object FuturesRecovery extends App {
	val netiquetteUrl = "https://www.ietf.org/rfc/rfc1855-badurl.txt"
	val netiquette = Future {Source.fromURL(netiquetteUrl).mkString}

	val answer = netiquette recover {
		case e: java.io.FileNotFoundException => "You know we can recovery from these kind of errors, so no probs, keep up the good work"
	}
	answer foreach log
	Thread.sleep(1000)
}