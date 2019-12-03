package example

import cats.effect.Resource
import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.schedulers.CanBlock
import scala.concurrent.duration._

object Hello extends App {
  val s = Scheduler.io()
  try {
    Resource
      .pure[Task, Scheduler](s)
      .use { implicit fixture =>
        Task.delay {
          Task
            .delay(println("hello world"))
            .timeout(10.seconds)
            .runToFuture
            .result(10.seconds)(null)
        }
      }
      .runSyncUnsafe()(s, CanBlock.permit)
  } finally {
    s.shutdown()
  }
}
