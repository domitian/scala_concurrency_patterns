package org.learningconcurrency.chapter3

import org.learningconcurrency._
import scala.annotation.tailrec

import scala.collection._
import java.util.concurrent.atomic._
import java.util.concurrent._
import scala.collection.convert.decorateAsScala._
import java.io.File
import org.apache.commons.io.FileUtils
/* If a thread is creating file, it cannot be copied or deleted
If a file is being copied by one or more threads, it cannot be deleted.
If a file is being deleted, it cannot be copied.
A file can be deleted by only one thread
*/

class Entry(val isDir: Boolean){
	val state = new AtomicReference[State](new Idle)
}

sealed trait State

class Idle extends State
class Creating extends State
class Copying(val n: Int) extends State
class Deleting extends State

class Filesystem(val root: String) {

	val rootDir = new File(root)
	@tailrec private def prepareForDelete(entry: Entry): Boolean = {
		val s0 = entry.state.get
		s0 match {
			case i: Idle => if (entry.state.compareAndSet(s0, new Deleting)) true else prepareForDelete(entry)
			case c: Creating => logMessage("File being created, so not possible to delete now"); false
			case c: Copying => logMessage("File being copied, so not possible to delete now"); false
			case d: Deleting => false
		}
	}

	private val messages = new LinkedBlockingQueue[String]
	val logger = new Thread {
		setDaemon(true)
		override def run() = while(true) log(messages.take)
	}
	logger.start


	def logMessage(s: String) = messages.offer(s)

	val files: concurrent.Map[String, Entry] = new ConcurrentHashMap().asScala
	for (f <- FileUtils.iterateFiles(rootDir, null, false).asScala)
		files.put(f.getName, new Entry(false))

	def deleteFile(filename: String): Unit = {
		files.get(filename) match {
			case None => logMessage(s"$filename doesnot exist")
			case Some(entry)  if entry.isDir => logMessage(s"$filename is a directory, so cannot be deleted")
			case Some(entry) => execute {
				if (prepareForDelete(entry))
					if (FileUtils.deleteQuietly(new File(filename)))
						files.remove(filename)
			}
		}
	}

	@tailrec private def acquire(entry: Entry): Boolean = {
		val cn = entry.state.get
		cn match {
			case _: Creating | _:Deleting => logMessage("File inaccessible, cannot be deleted"); false
			case i: Idle => if (entry.state.compareAndSet(cn, new Copying(1))) true else acquire(entry)
			case c: Copying => if (entry.state.compareAndSet(cn, new Copying(c.n+1))) true else acquire(entry)
		}
	}

	@tailrec private def release(entry: Entry): Unit = {
		val cn = entry.state.get
		cn match {
			case c: Creating => if (!entry.state.compareAndSet(cn, new Idle)) release(entry)
			case c: Copying =>  {
				val nstate = if (c.n == 1) new Idle else new Copying(c.n-1)
				if (!entry.state.compareAndSet(cn, nstate)) release(entry)
			}
		}
	}

	def copyFile(src: String, dst: String): Unit = {
		files.get(src) match {
			case Some(srcEntry) if (!srcEntry.isDir)=>  execute {
				if (acquire(srcEntry)) try{
					val dstEntry = new Entry(false)
					dstEntry.state.set(new Creating)
					if (files.putIfAbsent(dst, dstEntry) == None) try {
						FileUtils.copyFile(new File(src), new File(dst))
					} finally release(dstEntry)
				} finally release(srcEntry)
			}
		}
	}
}
object FilesystemRunner extends App{
	val filesystem = new Filesystem(".")
	filesystem.logMessage("Filesystem started")
	filesystem.deleteFile("test.txt")
}