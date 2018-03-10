package org.learningconcurrency.chapter3

import org.learningconcurrency._

/* This cause thread starvation and resource under utilization as threads are put
// to sleep and do nothing. So worker thread will run this until the job is completed
// since each thread is just sleeping, it can cause starvation. As by default forkjoin
// pool has 8 worker threads, so they run for 2 seconds, until then nothing else gets
executed by forkjoin pool.
*/
object ExecutionContextSleep extends App {
	for (i <- 1 to 32) execute {
		Thread.sleep(2000)
		log(s"Task $i completed")
	}
	Thread.sleep(10000)
}