package org.learningconcurrency

object Chap2 extends App{

	// 1
	def parallel[A,B](a: =>A, b: =>B): (A, B) = {
		var x: Option[A] = None
		var y: Option[B] = None
		val t1 = thread{
			println("Running in thread1")
			x = Some(a)
		}
		val t2 = thread{
			println("Running in thread2")
			y = Some(b)
		}
		t1.join(); t2.join()
		(x.get, y.get)
	}
	println(parallel[Int, Int]({1+1}, {2+2}))

	// 2
	def periodically(duration: Long)(b: =>Unit): Unit = {
		while (true){
			thread{
				println("executing in new thread")
				b
			}
			println(s"sleeping in main thread for $duration")
			Thread.sleep(duration)
		}
	}
	// periodically(1000)({
	// 	val a = 1
	// 	a*a
	// })

	// 3 and 4 and 5
	@throws(classOf[Exception])
	class SyncVar[T] {
		private var i: Option[T] = None
		private val lock = new AnyRef
		def get(): T = {
			val o = i.get
			i = None
			o
		}

		def put(x: T): Unit = {
			if (i!=None) throw new Exception
			i = Some(x)
		}

		def isEmpty(): Boolean = {
			if (i == None) true else false
		}

		def nonEmpty(): Boolean = !(isEmpty)

		def getWait(): T = {
			lock.synchronized {
				while (isEmpty) lock.wait
				val out = get
				lock.notify
				out
			}
		}

		def putWait(x: T): Unit = {
			lock.synchronized {
				while (nonEmpty) lock.wait
				put(x)
				lock.notify
			}
		}
	}
	val a = new  SyncVar[Int]()
	a.put(1)
	log(a.get.toString)
	log(a.isEmpty.toString)
	log(a.nonEmpty.toString)
	// a.get

	//4 - producer consumer
	def producerConsumer() = {
		val syncvar = new SyncVar[Int]()
		val lock = new AnyRef
		def producer() = thread {lock.synchronized {
			(0 to 15).toList.foreach { i =>
				while (syncvar.nonEmpty) lock.wait
				if (!syncvar.nonEmpty){
					syncvar.put(i)
					lock.notify
				}
			} 
		} }

		// consumer also reading for 15 times so that it can exit after 15 times
		def consumer() = thread {lock.synchronized {
			(0 to 15).toList.foreach { i =>
				while (syncvar.isEmpty) lock.wait
				if (syncvar.nonEmpty){
					println(syncvar.get)
					lock.notify
				}
			}
		} }

		val pt = producer
		val ct = consumer
		pt.join(); ct.join()
	}
	producerConsumer

	def producerConsumerWithWait() = {
		val syncvar = new SyncVar[Int]()
		val lock = new AnyRef
		def producer() = thread {
			(0 to 15).toList.foreach { i =>
				syncvar.putWait(i)
			} 
		}

		// consumer also reading for 15 times so that it can exit after 15 times
		def consumer() = thread {
			(0 to 15).toList.foreach { i =>
				println(syncvar.getWait)
			}
		}

		val pt = producer
		val ct = consumer
		pt.join(); ct.join()
	}
	producerConsumerWithWait


	//6

	class SyncQueue[T](n: Int) {
		import scala.collection.mutable.Queue
		private val queue = new Queue[T]
		private val lock = new AnyRef
		def get(): T = {
			queue.dequeue
		}

		def put(x: T): Unit = {
			if (queue.length==n) throw new Exception
			queue.enqueue(x)
		}

		def isEmpty(): Boolean = {
			if (queue.length == 0) true else false
		}

		// Is full
		def nonEmpty(): Boolean = if (queue.length == n) true else false

		def getWait(): T = {
			lock.synchronized {
				while (isEmpty) lock.wait
				val out = get
				lock.notify
				println(out)
				out
			}
		}

		def putWait(x: T): Unit = {
			lock.synchronized {
				while (nonEmpty) lock.wait
				put(x)
				lock.notify
			}
		}

	}
	println("running exercise 6")
	val q = new SyncQueue[Int](3)
	q.put(1)
	q.put(2)
	q.put(3)
	val t1 = thread{q.putWait(4)}
	val t2 = thread{q.getWait}
	t1.join(); t2.join()


	// 7
	import org.learningconcurrency.SynchronizedNesting.Account
	def send(a: Account, b: Account, amount: Int) = {
		def adjust(){
			a.money -= amount
			b.money += amount
		}
		if (a.uid < b.uid)
			a.synchronized {b.synchronized{ adjust() }}
		else
			b.synchronized {a.synchronized { adjust() }}
	}

	def sendAll(accounts: Set[Account], target: Account): Unit = {
		val tj = accounts.map { account =>
			thread{send(account, target, account.money)}
		}
		tj.foreach{t => t.join()}
	}
	val jane = new Account("Jane", 100)
	val john = new Account("John", 200)
	val tara = new Account("Jane", 100)
	val target = new Account("John", 200)
	sendAll(Set(jane, john, tara), target)
	println(target.money)


	// 8, 9, 10

	class PriorityTaskPool(p: Int, important: Int) {
		import scala.collection._
		import scala.math._
		def taskPriority(a: (Int, ()=>Unit)) = a._1
		private val tasks = mutable.PriorityQueue[(Int, () => Unit)]()(Ordering.by(taskPriority))
		private var terminated = false
		val workers = (1 to p) map { k =>
			thread {
				def poll(): Option[(Int, () => Unit)] = tasks.synchronized {
					while (tasks.isEmpty && !terminated) tasks.wait
					if (!terminated || (!tasks.isEmpty && tasks.head._1 >= important)) Some(tasks.dequeue) else None
				}
				while(true && (!terminated || (!tasks.isEmpty && tasks.head._1 >= important) )) poll match {
					case Some((p, task)) => task()
					case None => 
				}
			}
		}

		def asynchronous(priority: Int)(task: =>Unit): Unit = tasks.synchronized {
			tasks.enqueue((priority, () => task))
			println(s"task added to queue $tasks")
			tasks.notify
		}

		def shutdown() = tasks.synchronized {
			terminated = true
			println("shutting down all workers safely")
			1 to p foreach(k => tasks.notify)
		}
	}
	val pp = new PriorityTaskPool(1,5)
	pp.asynchronous(10)({ Thread.sleep(1000);println("world 10")})
	pp.asynchronous(1)({Thread.sleep(1000); println("Heloo 1")})
	pp.asynchronous(5)({Thread.sleep(1000);println("world 5")})
	// Thread.sleep(5000)
	// Will shutdown all threads only after executing high priority threads which are above the important given
	pp.shutdown


}