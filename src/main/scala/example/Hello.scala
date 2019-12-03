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
      .pure[Task, Scheduler](s) // works OK if replaced by cats IO
      .use { implicit fixture =>
        Task.delay {
          Task
            .delay(println("hello world"))
            .timeout(10.seconds) // works as expected if removed
//            .executeAsync // works OK if uncommented
            .runToFuture
            .result(10.seconds)(null)
        }
      }
      .runSyncUnsafe()(s, CanBlock.permit)
  } finally {
    s.shutdown()
  }
}
