package org.learningconcurrency.chapter5
import org.learningconcurrency._

import scala.collection._
import scala.util.Random

import scala.concurrent.forkjoin.ForkJoinPool

object ParConfig extends App{
    val fjPool = new ForkJoinPool(2)
    val customTaskSupport = new parallel.ForkJoinTaskSupport(fjPool)
    val numbers = Random.shuffle(Vector.tabulate(500000)(i => i))
    val partime = timed{
        val parNum = numbers.par
        parNum.tasksupport = customTaskSupport
        val n = parNum.max
        println(s"largest number $n")
    }
    println(s"parallel time is $partime")
}