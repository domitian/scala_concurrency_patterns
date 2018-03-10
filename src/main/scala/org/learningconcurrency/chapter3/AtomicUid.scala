package org.learningconcurrency.chapter3
import scala.annotation.tailrec

import java.util.concurrent.atomic._
import org.learningconcurrency._

object AtomicUid extends App {
	private val uid = new AtomicLong(0L)
	def getUniqueUid(): Long = uid.incrementAndGet()
	execute({log(s"getting new uid in async ${getUniqueUid}")})
	log(s"getting new uid in sync ${getUniqueUid}")

	/* Internally incrementAndGet is implemented by compareAndSet, as it is the building block
	So implementing custom atomic adder */

	private val i = new AtomicLong(0L)
	@tailrec def incrementVal(): Long = {
		val oldi = i.get
		val newi = oldi + 1
		if (i.compareAndSet(oldi, newi)) newi else incrementVal
	}
	for(j <- 1 to 10) execute{
		incrementVal
		log(s"Incrementing i atomically in task $j value of i is $i")
	}
}