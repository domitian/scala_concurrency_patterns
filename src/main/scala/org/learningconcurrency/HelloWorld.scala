package org.learningconcurrency
import org.learningconcurrency._

object HelloWorld extends App {
  println("Hello World!")
  val t = thread{
  	println("i am inside thread")
  }
  t.join()
}
