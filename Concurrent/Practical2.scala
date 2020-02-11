import io.threadcso._
import scala.language.postfixOps
import scala.util.Random
import scala.collection.mutable._


/** Simulation of the Dining Philosophers example. */
object Task1{
  val N = 5 // Number of philosophers

  // Simulate basic actions
  def Eat = Thread.sleep(500)
  def Think = Thread.sleep(scala.util.Random.nextInt(900))
  def Pause = Thread.sleep(500)

  // Each philosopher will send "pick" and "drop" commands to her forks, which
  // we simulate using the following values.
  type Command = Boolean
  val Pick = true; val Drop = false
 
  /** A single philosopher. */
  def phil(me: Int, left: ![Command], right: ![Command]) = proc("Phil"+me){
    repeat{
      Think
      println(me+" sits"); Pause
      left!Pick; println(me+" picks up left fork"); Pause
      right!Pick; println(me+" picks up right fork"); Pause
      println(me+" eats"); Eat
      left!Drop; Pause; right!Drop; Pause
      println(me+" leaves")
    }
  }

  /** A single fork. */
  def fork(me: Int, left: ?[Command], right: ?[Command]) = proc("Fork"+me){
    serve(
      left =?=> {
        x => assert(x == Pick); val y = left?; assert(y == Drop)
      }
      |
      right =?=> {
        x => assert(x == Pick); val y = right?; assert(y == Drop)
      }
    )
  }

  /** The complete system. */
  def system = {
    // Channels to pick up and drop the forks:
    val philToLeftFork, philToRightFork = Array.fill(5)(OneOne[Command])
    // philToLeftFork(i) is from Phil(i) to Fork(i);
    // philToRightFork(i) is from Phil(i) to Fork((i-1)%N)
    var allPhils = || ( 
      for (i <- 1 until N)
      yield phil(i, philToLeftFork(i), philToRightFork(i))
    )

    // the first philosopher will start with his right hand
    allPhils = allPhils || phil(0, philToRightFork(0), philToLeftFork(0))
    val allForks = || ( 
      for (i <- 0 until N) yield
        fork(i, philToRightFork((i+1)%N), philToLeftFork(i))
    )
    allPhils || allForks
  }

  /** Run the system. */
  //def main(args : Array[String]) = { system() }
}

object Task2{
    val N = 5 // Number of philosophers

    // Simulate basic actions
    def Eat = Thread.sleep(500)
    def Think = Thread.sleep(scala.util.Random.nextInt(900))
    def Pause = Thread.sleep(500)

    // Each philosopher will send "pick" and "drop" commands to her forks, which
    // we simulate using the following values.
    type Command = Boolean
    val Pick = true; val Drop = false
    var waitingList = Queue[Int]();


    def butler(receivingChan: ?[(Int,Command)], replyChan: List[![Unit]]) = proc("butler"){
        //cntSeated = the numbers of the currently sat philosophers
        var cntSeated = 0
        repeat{
            val (i, com) = receivingChan?();
            assert(0 <= i && i < N)
            if(com)
            {
                //trying to pick up
                if(cntSeated < N-1)
                {
                    cntSeated += 1;
                    replyChan(i)!();
                }
                else
                {
                    val aux = waitingList.enqueue(i)
                } 
            }
            else
            {
                cntSeated -= 1
                if(!waitingList.isEmpty)
                {
                    cntSeated += 1
                    replyChan(waitingList.dequeue)!();
                }
            }
        }
        
    }

    /** A single philosopher. */
    def phil(me: Int, left: ![Command], right: ![Command], toButler: ![(Int, Command)], fromButler: ?[Unit]) = proc("Phil"+me){
        repeat{
            toButler!((me, Pick))
            fromButler?()
            Think
            println(me+" sits"); Pause
            println(me + " is asking for a left fork")
            left!Pick
            println(me+" picks up left fork")
            Pause
            println(me + " is asking for a right fork")
            right!Pick
            println(me+" picks up right fork")
            println(me+" eats"); Eat
            left!Drop;  Pause; right!Drop; println(me + " drops right fork"); Pause
            toButler!((me, Drop));
            println(me+" leaves")
        }
    }

    /** A single fork. */
    def fork(me: Int, left: ?[Command], right: ?[Command]) = proc("Fork"+me){
        serve(
        left =?=> {
            x => assert(x == Pick); val y = left?; assert(y == Drop)
        }
        |
        right =?=> {
            x => assert(x == Pick); val y = right?; assert(y == Drop)
        }
        )
    }

    /** The complete system. */
    def system = {
        // Channels to pick up and drop the forks:
        val philToLeftFork, philToRightFork = List.fill(5)(OneOne[Command])
        val replyChan = List.fill(5)(OneOne[Unit])
        val toButler = ManyOne[(Int, Command)]
        // philToLeftFork(i) is from Phil(i) to Fork(i);
        // philToRightFork(i) is from Phil(i) to Fork((i-1)%N)
        val allPhils = || ( 
        for (i <- 0 until N)
        yield phil(i, philToLeftFork(i), philToRightFork(i), toButler, replyChan(i))
        )
        val allForks = || ( 
        for (i <- 0 until N) yield
            fork(i, philToRightFork((i+1)%N), philToLeftFork(i))
        )
        allPhils || allForks || butler(toButler, replyChan)
    }

    /** Run the system. */
    //def main(args : Array[String]) = { system() }
}

object Task3{
  val N = 5 // Number of philosophers

  // Simulate basic actions
  def Eat = Thread.sleep(500)
  def Think = Thread.sleep(scala.util.Random.nextInt(900))
  def Pause = Thread.sleep(500)

  // Each philosopher will send "pick" and "drop" commands to her forks, which
  // we simulate using the following values.
  type Command = Boolean
  val Pick = true; val Drop = false
 
  /** A single philosopher. */
  def phil(me: Int, leftPick: channel.DeadlineManyOne[Command], leftDrop: ![Command], rightPick: channel.DeadlineManyOne[Command], rightDrop: ![Command]) = proc("Phil"+me){
    repeat{
      Think
      println(me+" sits"); Pause
      leftPick!Pick; println(me+" picks up left fork"); Pause
      val att = rightPick.writeBefore(5000)(Pick)
      if(att)
      {
        println(me+" picks up right fork"); Pause
        println(me+" eats"); Eat
        leftDrop!Drop; Pause; rightDrop!Drop; Pause
        println(me+" leaves")
      }
      else
      {
          //drop left 
          leftDrop!Drop
          println(me + " leaves")
      }
      
    }
  }

  /** A single fork. */
  def fork(me: Int, pick: channel.DeadlineManyOne[Command], drop: ?[Command]) = proc("Fork"+me){
    repeat{
        val x:Command = pick?
        val y:Command = drop?
    }
  }

  /** The complete system. */
  def system = {
    // Channels to pick up and drop the forks:
    val philToForkPick = Array.fill(5)(new channel.DeadlineManyOne[Command])
    val philToForkDrop = Array.fill(5)(OneOne[Command])
    // philToLeftFork(i) is from Phil(i) to Fork(i);
    // philToRightFork(i) is from Phil(i) to Fork((i-1)%N)
    val allPhils = || ( 
      for (i <- 0 until N)
      yield phil(i, philToForkPick((N- i - 1) % N), philToForkDrop((N- i - 1) % N), philToForkPick(i), philToForkDrop(i) )
    )
    val allForks = || ( 
      for (i <- 0 until N) yield
        fork(i, philToForkPick(i), philToForkDrop(i))
    )
    allPhils || allForks
  }

  /** Run the system. */
  def main(args : Array[String]) = { system() }
}
