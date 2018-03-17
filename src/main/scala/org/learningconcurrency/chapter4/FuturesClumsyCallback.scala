package org.learningconcurrency.chapter4
import org.learningconcurrency._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.io.Source

import java.io._
import org.apache.commons.io.FileUtils._
import scala.collection.convert.decorateAsScala._

object FuturesClumsyCallback extends App{
	def blacklistFile(name: String): Future[List[String]] = Future {
		val lines = Source.fromFile(name).getLines
		lines.filter(x => !x.startsWith("#") && !x.isEmpty).toList
	}

	def findFiles(patterns: List[String]): List[String] = {
		val root = new File(".")
		for {
			f <- iterateFiles(root, null, true).asScala.toList
			pat <- patterns
			abspat = root.getCanonicalPath + File.separator + pat
			if f.getCanonicalPath.contains(abspat)
		} yield f.getCanonicalPath
	}

	blacklistFile(".gitignore") foreach {
		case lines => 
			val files = findFiles(lines)
			log(s"matches: ${files.mkString("\n")}")
	}
	Thread.sleep(1000)

	// Using map to map future value to generate another future
	// to better understand, a map maps elements of collection to new collection by applying a function
	// so imagine future as collection, result of future as elements of collection/future so we get a new future/collection
	def blacklisted(name: String): Future[List[String]] = 
		blacklistFile(name).map(patterns => findFiles(patterns))
}