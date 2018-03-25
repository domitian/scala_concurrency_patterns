package org.learningconcurrency.chapter5
import org.learningconcurrency._

import scala.collection._

object NonParallelizable extends App{
    val seqv = Vector.fill(1000000)("")
    val seql = List.fill(1000000)("")
    log(s"parallel time for vector ${timed(seqv.par)} ms")
    log(s"parallel time for list ${timed(seql.par)} ms")
}