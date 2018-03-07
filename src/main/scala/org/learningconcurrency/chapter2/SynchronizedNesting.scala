package org.learningconcurrency

import org.learningconcurrency._

object SynchronizedNesting extends App{
	import scala.collection._
	import org.learningconcurrency.protectedUids.getUniqueUid
	private val transfers = mutable.ArrayBuffer[String]()

	class Account(val name: String, var money: Int){
		val uid = getUniqueUid()
	}

	def add(account: Account, amount: Int) = account.synchronized {
		account.money += amount
		if (amount>10) logTransfer(account.name, amount)
	}
	def logTransfer(name: String, amount: Int) = transfers.synchronized {
		transfers += s"transferred to account $name : $amount"
	}

	// Execution
	val jane = new Account("Jane", 100)
	val john = new Account("John", 200)
	val t1 = thread { add(jane, 5) }
	val t2 = thread { add(john, 50) }
	val t3 = thread { add(jane, 70) }
	t1.join(); t2.join(); t3.join() 
	log(s"--- transfers ---\n$transfers")

}
