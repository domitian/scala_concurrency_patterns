package org.learningconcurrency.chapter3

import org.learningconcurrency._

/* Lazy vals are initialized only when used, and the same is true for singleton objects
as well. Under the hood, they are implemented using lazy vals.
So we need to be careful about using lazy vals/singletons in threads, because of non determinism.
For example if we have thread specific code in lazy val, then it will cause issues
or in case of cyclic dependencies, it will cause a deadlock
*/

// This causes deadlock in threaded program because of cyclic dependency
object LazyValDeadlock extends App{
	object A {lazy val a: Int = B.b}
	object B {lazy val b: Int = A.a}
	execute{B.b}
	A.a
}