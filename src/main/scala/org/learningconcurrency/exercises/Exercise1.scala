package org.learningconcurrency


object Excercise1 extends App {
	def square(x: Int) = x*x
	println(square(2))
	// 1
	def compose[A, B, C](g: B=>C, f: A=>B): A=>C = (x: A) => g(f(x))
	//2
	def fuse[A, B](a: Option[A], b: Option[B]): Option[(A, B)] = for(x<-a; y<-b) yield((x, y))
	//3
	def check[T](xs: Seq[T])(pred: T => Boolean): Boolean = {
		var x = true
		xs.foreach(el => x = x && pred(el))
		x
	}

	val greaterThan2 = (x: Int )=> if (x>2)  true else false
	println(check[Int](Seq(3))(greaterThan2))
	println(check(1 until 10)(40 / _  > 0))
	//4


	//5
	def permutations(s: String): Seq[String] = {
		def permute(substr: String, visited: Array[Boolean]): Seq[String] = {
			if (substr.length == s.length) return Seq(substr)
			var sa = Seq[String]()
			for (i <- 0 until s.length){
				if (!visited(i)) {
					visited(i) = true
					sa = sa ++ permute(substr+s(i).toString, visited)
					visited(i) = false
				}
			}
			sa
		}
		var visited = Array.ofDim[Boolean](s.length)
		val k = permute("", visited)
		println(k)
		k
	}
	permutations("abcd")
}