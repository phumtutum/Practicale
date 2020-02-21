import io.threadcso._
import scala.collection.mutable.Stack


/** A partial queue that terminates if all worker threads are attempting to
  * dequeue, and the queue is empty.
  * @param numWorkers the number of worker threads. */
class TerminatingPartialStack[A](numWorkers: Int){
  /** Channel for enqueueing. */
  private val pushChan = ManyOne[A]

  private type ReplyChan = Chan[A]

  /** Channel for dequeueing. */
  private val popChan = ManyOne[ReplyChan]

  /** Channel for shutting down the queue. */
  private val shutdownChan = ManyOne[Unit]

  /** Enqueue x.
    * @throws StopException if the queue has been shutdown. */
  def push(x: A): Unit = pushChan!x

  /** Attempt to dequeue a value.
    * @throws StopException if the queue has been shutdown. */
  def pop: A = {
    val reply = OneOne[A]
    popChan!reply
    reply?()
  }

  /** Shut down this queue. */
  def shutdown = attempt{ shutdownChan!(()) }{ }
  // Note: it's possible that the server has already terminated, in which case
  // we catch the StopException.

  /** The server process. */
  private def server = proc("server"){
    // Currently held values
    val stack = new Stack[A]()
    // Queue holding reply channels for current dequeue attempt.
    val waiters = new Stack[ReplyChan]()
    // Inv: stack.isEmpty or waiters.isEmpty
    // Termination: signal to all waiting workers
    def close = {
      for(c <- waiters) c.close
      pushChan.close; popChan.close; shutdownChan.close
    }

    serve(
      pushChan =?=> { x => 
        if(waiters.nonEmpty){ // pass x directly to a waiting dequeue
          assert(stack.isEmpty); waiters.push!x
        }
        else stack.push(x)
      }
      |  
      popChan =?=> { reply =>
        if(stack.nonEmpty) reply!(stack.pop) // service request immediately
        else{
          waiters.push(reply)
          if(waiters.length == numWorkers) close
        }
      }
      |
      shutdownChan =?=> { _ => close }
    )
  }

  server.fork
}


//STARTING CONCURRENT SEARCH


/** A class to search Graph g concurrently. */
class ConcGraphSearch[N](g: Graph[N]) extends GraphSearch[N](g){
  /**The number of workers. */
  val numWorkers = 8

  /** Try to find a path in g from start to a node that satisfies isTarget. */
  def apply(start: N, isTarget: N => Boolean): Option[N] = {
    // Queue storing edges and the path leading to that edge
    val stack = new TerminatingPartialStack[N](numWorkers)
    stack.push(start)

    // Channel on which a worker tells the coordinator that it has found a
    // solution.
    val pathFound = ManyOne[N]

    // A single worker
    def worker = proc("worker"){
      repeat{
        val (n, path) = stack.pop
        for(n1 <- g.succs(n)){
          if(isTarget(n1)) pathFound!(n1) // done!
          else stack.push(n1)
        }
      }
      pathFound.close // causes coordinator to close down
    }

    // Variable that ends up holding the result; written by coordinator. 
    var result: Option[N] = None

    def coordinator = proc("coordinator"){
      attempt{ result = Some(pathFound?()) }{ }
      stack.shutdown // close queue; this will cause most workers to terminate
      pathFound.close // in case another thread has found solution
    }

    val workers = || (for(_ <- 0 until numWorkers) yield worker)
    (workers || coordinator)()
    result
  }
}
