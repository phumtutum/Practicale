// Template for the Sleeping Tutor practical

import io.threadcso._
import scala.util.Random



/** The trait for a Sleeping Tutor protocol. */
trait SleepingTutor{
  /** A tutor waits for students to arrive. */
  def tutorWait

  /** A student arrives and waits for the tutorial. */
  def arrive
  
  /** A student receives a tutorial. */
  def receiveTute

  /** A tutor ends the tutorial. */
  def endTeach
}

class SleepingTutorSemaphor extends SleepingTutor {
    
    val studentReady, finishReceived, tuteEnded, studentArrived, startTute, recTute = BooleanSemaphore(available = false);

    def tutorWait = {
        studentReady.up
        studentArrived.down
        studentReady.up
        studentArrived.down
        startTute.up
    }

    def arrive = {
        studentReady.down
        studentArrived.up
    }

    def receiveTute = {
        tuteEnded.down        
        finishReceived.up
    }

    def endTeach = {
        startTute.down
        tuteEnded.up
        finishReceived.down
        tuteEnded.up
        finishReceived.down
    }
}

object SleepingTutorSimulation{
  private val st: SleepingTutor = new SleepingTutorSemaphor

  def student(me: String) = proc("Student"+me){
    while(true){
      Thread.sleep(Random.nextInt(2000))
      println("Student "+me+" arrives")
      st.arrive
      println("Student "+me+" ready for tutorial")
      st.receiveTute
      println("Student "+me+" leaves")
    }
  }

  def tutor = proc("Tutor"){
    while(true){
      println("Tutor waiting for students")
      st.tutorWait
      println("Tutor starts to teach")
      Thread.sleep(1000)
      println("Tutor ends tutorial")
      st.endTeach
      Thread.sleep(1000)
    }
  }

  def system = tutor || student("Alice") || student("Bob")

  def main(args: Array[String]) = system()
}
