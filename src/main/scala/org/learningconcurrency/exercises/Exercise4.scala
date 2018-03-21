package org.learningconcurrency.chapter4

import org.learningconcurrency._
import scala.annotation.tailrec
import scala.concurrent._

import scala.collection.concurrent._
import scala.concurrent.duration._
import java.util.concurrent.atomic._
import ExecutionContext.Implicits.global
import scala.io._
import java.util._
import scala.util._

object Exercise4 extends App {

	// 1
	val input = readLine("Please input a url:-\n")
	def urlTxt(url: String): Future[String] = Future{
		// Thread.sleep(1000)
		Source.fromURL(url).getLines.mkString("\n")
	}
	val urltxt = urlTxt(input)
	private val timer = new Timer(true)

	def timeout(t: Long): Promise[Unit] = {
		val p = Promise[Unit]
		timer.schedule(new TimerTask {
			def run() = {
				if (p.isCompleted)
					timer.cancel
				else
					log(".")

			}
		}, t, t)
		p
	}
	val cancel = timeout(50)
	val result = Await.result(urltxt, 2.seconds)
	cancel success ()
	// log(result) // - To Print the result

	// 2
	class IVar[T] {
		private val p = Promise[Unit]
		private var value: Option[T] = None
		def apply(): T = if (p.isCompleted) value.get else throw new Exception

		def :=(x: T): Unit = p success {value = Some(x)}
	}

	val a = new IVar[Int]
	a := 1
	log(s"${a.apply}")
	// a := 2 // throws an exception

	// 3
	object FutureExtendCompose{
		implicit class FutureOps[T](self: Future[T]){
			def exists(p: T => Boolean): Future[Boolean] = self.map(fResult => p(fResult))
		}
	}
	/* Commenting below code because next exercise also uses similar method */
	// import FutureExtendCompose._
	// val f = Future{4}.exists((x: Int) => x %2 == 0)
	// f.foreach { case x => log(x.toString)}

	// 4
	object FutureExtendPromise {
		implicit class FutureOps[T](self: Future[T]){
			private val selfPromise = Promise[Boolean]
			def exists(p: T => Boolean): Future[Boolean] = {
				self onComplete {
					case Success(x) => selfPromise success p(x)
					case Failure(error) => selfPromise failure new Exception(error)
				}
				selfPromise.future
			}

		}
	}
	import FutureExtendPromise._
	val pf = Future{4}.exists((x: Int) => x %2 == 0)
	pf.foreach { case x => log(x.toString)}
	Thread.sleep(100)

	// y
	import scala.collection._
	import scala.collection.convert.decorateAsScala._
	import java.util.concurrent.ConcurrentHashMap
	class IMap[K, V] {
		private val realMap: concurrent.Map[K, V] = new ConcurrentHashMap().asScala
		private val promiseMap: concurrent.Map[K, Promise[V]] =  new ConcurrentHashMap().asScala
		def update(k: K, v: V): Unit = if (realMap.putIfAbsent(k, v) != None) throw new Exception(s"invalid updating existing key $k") else {
				promiseMap.putIfAbsent(k, Promise[V])
				promiseMap(k) success v
			}
		def apply(k: K): Future[V] = {
			promiseMap.putIfAbsent(k, Promise[V])
			promiseMap(k).future
		}
	}

	//testing example
	val amap = new IMap[Int, Int]()
	val frs = for (i <- 0 to 1) yield Future{amap.update(1,2)}
	for (f <- frs) {
		f onComplete {
			case Success(x) => log(s"succeed in adding value $x")
			case Failure(error) => log(s"not succeeded in adding value $error")
		}
	}
	val p1 = amap(1)
	val p2 = amap(2)
	amap.update(2,1)
	Seq(p1, p2) foreach {x => 
		x foreach { value => log(s"got the keys value $value")}
	}
	Thread.sleep(200)


	// 8
	object PromiseExtend{
		implicit class PromiseOps[T](self: Promise[T]){
			def compose[S](f: S => T): Promise[S] = {
				val resultPromise = Promise[S]
				if (!self.isCompleted){
					resultPromise.future onComplete {
						case Success(x) => self success f(x)
						case Failure(error) => self failure error
					}
				}
				resultPromise
			}
		}
	}
	import PromiseExtend._
	val op = Promise[Int]
	val rp = op.compose[Int](x => 2*x)
	rp success 2
	op.future foreach {x => log(s"$x")}

}