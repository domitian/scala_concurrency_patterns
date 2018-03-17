package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.annotation.tailrec

import scala.io.Source
import scala.concurrent._
import ExecutionContext.Implicits.global

import org.apache.commons.io.monitor._

object FileMonitoring extends App {
	def fileCreated(directory: String): Future[String] = {
		val p = Promise[String]
		val fileMonitor = new FileAlterationMonitor(1000)
		val observer = new FileAlterationObserver(directory)
		val listener = new FileAlterationListenerAdaptor {
			override def onFileCreate(file: java.io.File): Unit = {
				try p.trySuccess(file.getName) finally fileMonitor.stop()
			}
		}
		observer.addListener(listener)
		fileMonitor.addObserver(observer)
		fileMonitor.start
		p.future
	}
	fileCreated(".") foreach {
		case filename => log(s"Detected new file $filename")
	}
}