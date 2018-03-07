package org.learningconcurrency

import org.learningconcurrency._

object SynchronizedDeadlock extends App{

	import org.learningconcurrency.SynchronizedNesting.Account
	def send(a: Account, b: Account, amount: Int) = a.synchronized {
		b.synchronized {
			a.money -= amount
			b.money += amount
		}
	}

	val a = new Account("jack", 1000)
	val b = new Account("Jill", 2000)

	val t1 = thread{for(i <- 0 until 100) send(a, b, 1)}
	val t2 = thread{for(i<- 0 until 100) send(b, a, 1)}
	t1.join(); t2.join();
	log(s"${a.name} : ${a.money}, ${b.name} : ${b.money}")
}

object SynchronizedDeadlockAvoid extends App{
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

	val a = new Account("jack", 1000)
	val b = new Account("Jill", 2000)

	val t1 = thread{for(i <- 0 until 100) send(a, b, 1)}
	val t2 = thread{for(i<- 0 until 100) send(b, a, 1)}
	t1.join(); t2.join();
	log(s"${a.name} : ${a.money}, ${b.name} : ${b.money}")
}