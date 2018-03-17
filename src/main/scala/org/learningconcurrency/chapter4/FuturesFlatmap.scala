package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source


object FuturesFlatmap extends App {

	val netiquetteUrl = "https://www.ietf.org/rfc/rfc1855.txt"
	val urlSpecUrl = "https://www.w3.org/Addressing/URL/url-spec.txt"
	val netiquette = Future {Source.fromURL(netiquetteUrl).mkString}
	val urlSpec = Future {Source.fromURL(urlSpecUrl).mkString}

	val answer = netiquette flatMap { nettxt =>
		urlSpec map {urltext => "Check this out: " + urltext + "and also this about ftp " + nettxt}
	}
	answer foreach {case contents => log(contents)}

	val anotherAnswer = for {
		nettxt <- netiquette
		urltxt <- urlSpec
	} yield{
		"First read this: " + nettxt + " and this: " + urltxt
	}
	anotherAnswer foreach log
	Thread.sleep(1000)
}