package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source

import scala.util.{Try, Failure, Success}

object FuturesTry extends App{
	def handleMessage(msg: Try[String]) = msg match {
		case Success(m) => log(s"success $m")
		case Failure(error) => log(s"failed with Exception $error")
	}

	val threadName: Try[String] = Try(Thread.currentThread.getName)
	val someMsg: Try[String] = Try("some random text")

	val message: Try[String] = for {
		tn <- threadName
		ms <- someMsg
	} yield s"Message $ms was created on t = $tn"

	handleMessage(message)

}