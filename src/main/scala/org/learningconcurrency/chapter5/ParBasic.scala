package org.learningconcurrency.chapter5
import org.learningconcurrency._

import scala.collection._
import scala.util.Random

/* Will speed execution for parallel collections when
collection is large and is parallelizable */
object ParBasic extends App{
	val numbers = Random.shuffle(Vector.tabulate(5000000)(i => i))
	val seqTime = timed {numbers.max}
	log(s"sequential time $seqTime")
	val parTime = timed {numbers.par.max}
	log(s"parallel time $parTime")
}