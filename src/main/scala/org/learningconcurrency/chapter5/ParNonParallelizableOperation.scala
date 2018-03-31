package org.learningconcurrency.chapter5
import org.learningconcurrency._

import scala.collection._
import scala.concurrent._
import ExecutionContext.Implicits.global

/* Since foldleft is not parallelizable operation as it traverses the
sequence from left to right. Since function of foldleft cannot take
two values of accumulator and merge them into new value it is not 
parallelizable, so same perf for both par and seq collections as
parallel collection will be run sequentially */
object ParNonParallelizableOperation extends App{
    ParHtmlSearch.getHtmlSpec foreach { case spec =>
        def allMatches(d: GenSeq[String]) = warmedTimed() {
            val results = d.foldLeft(" "){(acc, line) =>
                if (line.matches(".*TEXTAREA.*")) s"$acc\n$line" else acc
            }
        }
        /* Using aggregate will be faster for par collection as it doesn't have
        a specific order elements have to be traversed and accumulators can
        be merged because we can pass another function for accumulators to it.
        */
        def allMatchesWithAgg(d: GenSeq[String]) = warmedTimed() {
            val results = d.aggregate(" ")(
                (acc, line) => if (line.matches(".*TEXTAREA.*")) s"$acc\n$line" else acc,
                (acc1, acc2) => acc1 + acc2
            )
        }
        val seqTime = allMatches(spec)
        log(s"seqtime for all matches is $seqTime")
        val parTime = allMatchesWithAgg(spec.par)
        log(s"partime for all matches is $parTime")
    }
    Thread.sleep(3000)
}