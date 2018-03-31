package org.learningconcurrency.chapter5
import org.learningconcurrency._

import scala.collection._

/* For commutative oeprations ordering matters a lot. Hence it works
for arrays but not for set.*/
object ParNonCommutativeOperator extends App{
    val doc = mutable.ArrayBuffer.tabulate(20)(i => s"page $i")
    def test(doc: GenIterable[String]){
        val seqText = doc.seq.reduceLeft(_+_)
        val parText = doc.par.reduce(_+_)
        log(s"sequential text is $seqText")
        log(s"parallel text is $parText")
    }
    test(doc)
    test(doc.toSet)
}

/* Reduce gives non deterministic results for parallel because - is
a non associative operation */
object ParNonAssociativeOperator extends App{
    def test(doc: GenIterable[Int]) = {
        val seqText = doc.seq.reduceLeft(_-_)
        val parText = doc.par.reduce(_-_)
        log(s"sequential text is $seqText")
        log(s"parallel text is $parText")
    }
    test(0 to 10)
}