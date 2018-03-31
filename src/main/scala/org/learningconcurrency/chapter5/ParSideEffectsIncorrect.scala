package org.learningconcurrency.chapter5
import org.learningconcurrency._

import scala.collection._
/* This will probably have wrong result because we are using
mutable variable for keeping track of count, so like in concurrency
we have similar issue with shared memory if it's not exclusive*/
object ParSideEffectsIncorrect extends App{
    def intersection(a: GenSet[Int], b: GenSet[Int]): Int = {
        var total = 0
        for(x <- a) if (b contains x) total += 1
        total
    }
    val a = (0 until 1000).toSet
    val b = (0 until 1000 by 4).toSet
    log(s"seq result is ${intersection(a, b)}")
    log(s"par result is ${intersection(a.par, b.par)}")

    /* So instead of using synchronised or concurrent collection, we can
    use a parallel operation instead to make it faster and better than
    concurrent or synchronised one */
    val c = a.count(x => b contains x)
    log(s"result using parallel operation is $c")
}