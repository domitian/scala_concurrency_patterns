package org.learningconcurrency.chapter5
import org.learningconcurrency._

import scala.collection._
import scala.io._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._

object ParHtmlSearch extends App{
    def getHtmlSpec() = Future{
        val url = "https://www.w3.org/Addressing/URL/url-spec.txt"
        val spec = Source.fromURL(url)
        try spec.getLines.toArray
    }
    /* We should warmup jvm before to get accurate measurements
    as jvm might convert the frequently run code to machine code
    which gives us the best perf, so we should first warm up jvm
    , then measure else the times vary per run. So here we use a 
    warmedTime measuring defined in base object for accurate
    measurements */
    getHtmlSpec foreach { case specDoc =>
        def search(d: GenSeq[String]): Double = 
            warmedTimed() {d.indexWhere(line => line.matches(".*TEXTARE.*"))}
        val seqtime = search(specDoc)
        log(s"Sequential time is $seqtime")
        val partime = search(specDoc.par)
        log(s"parallel time is $partime")
    }
    Thread.sleep(4000)
}