package org.learningconcurrency

object UnprotectedUids extends App{
	var uidCount = 0L
	def getUniqueUid() = {
		val freshUid = uidCount+1
		uidCount = freshUid
		freshUid
	}


	def printUids(n: Int): Unit = {
		val uids = for(i <- 0 until n) yield getUniqueUid()
		log(s"uids: $uids")
	}
	val t = thread{printUids(5)}
	printUids(5)
	t.join()
}

object protectedUids extends App{
	var uidCount = 0L
	def getUniqueUid() = {
		val freshUid = uidCount+1
		uidCount = freshUid
		freshUid
	}

	def getUniqueUidWithSync() = this.synchronized{getUniqueUid()}

	def printUids(n: Int): Unit = {
		val uids = for(i <- 0 until n) yield getUniqueUidWithSync()
		log(s"uids: $uids")
	}
	val t = thread{printUids(5)}
	printUids(5)
	t.join()
}