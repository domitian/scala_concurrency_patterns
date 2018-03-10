package org.learningconcurrency

import scala.concurrent._

package object chapter3 {
	def execute(body: =>Unit) = ExecutionContext.global.execute(
		new Runnable{def run() = body}
	)
}