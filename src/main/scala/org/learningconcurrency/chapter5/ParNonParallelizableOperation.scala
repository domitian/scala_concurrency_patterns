package org.learningconcurrency.chapter5
import org.learningconcurrency._

import scala.collection._
import scala.concurrent._
import ExecutionContext.Implicits.global

/* Since foldleft is not parallelizable operation as it traverses the
sequence from left to right. Since function of foldleft cannot take
two values of accumulator and merge them into new value it is not 
parallelizable */
object ParNonParallelizableOperation extends App{
    ParHtmlSearch.getHtmlSpec foreach { case spec =>
        def allMatches(d: GenSeq[String]) = warmedTimed() {
            val results = d.foldLeft(" "){(acc, line) =>
                if (line.matches(".*TEXTAREA.*")) s"$acc\n$line" else acc
            }
        }
        val seqTime = allMatches(spec)
        log(s"seqtime for all matches is $seqTime")
        val parTime = allMatches(spec.par)
        log(s"partime for all matches is $parTime")
    }
    Thread.sleep(3000)
}