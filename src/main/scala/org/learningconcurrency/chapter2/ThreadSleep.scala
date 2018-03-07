package org.learningconcurrency

import org.learningconcurrency._


// Deterministic
object ThreadSleep extends App{
	val t = thread{
		Thread.sleep(1000)
		log("running new output after one sec")
	}
	t.join()
	log("New thread joined")
}

object ThreadNondeterministic extends App{
	val t = thread{
		log("New thread running")
	}
	log("in the main")
	log("in the main")
	t.join()
	log("New thread joined")
}

object ThreadsCommunicate extends App{
	var result: String = null
	val t = thread{
		result = "I added this result"
	}
	t.join()
	log(result)
}