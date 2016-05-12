package chapter5

import akka.actor._

/**
  * Created by seed on 2016. 5. 12..
  */
object Main extends App {
  val system = ActorSystem("TestSystem")
  val ping = system.actorOf(Props[PingActor], "pingActor")
  ping.tell(Work, ActorRef.noSender)
  ping.tell(Reset, ActorRef.noSender)
}

object Work
object Done
object Reset

class PingActor extends Actor with Stash with ActorLogging {
  import context._

  val child = actorOf(Props[Ping1Actor], "ping1Actor")

  override def receive: Receive = initial

  def initial: Receive = {
    case Work =>
      child.tell(Work, self)
      become(zeroDone)
    case a @ _ =>
      stash()
  }

  def zeroDone: Receive = {
    case Done =>
      log.info("Recevied the first done")
      become(oneDone)
    case a @ _ =>
      stash()
  }

  def oneDone: Receive = {
    case Done =>
      log.info("Recevied the second done")
      unstashAll()
      become(allDone)
    case a @ _ =>
      stash()
  }

  def allDone: Receive = {
    case Reset =>
      log.info("Recevied a reset")
      become(initial)
  }
}

class Ping1Actor extends Actor with ActorLogging {
  import context._

  val child1 = actorOf(Props[Ping2Actor], "ping2Actor")
  val child2 = actorOf(Props[Ping3Actor], "ping3Actor")

  override def receive: Receive = {
    case Work =>
      log.info("Ping1 received work")

      child1.tell(Work, sender)
      child2.tell(Work, sender)
  }
}

class Ping2Actor extends Actor with ActorLogging {

  override def receive: Receive = {
    case Work =>
      log.info("Ping2 receive work")

      work
      sender().tell(Done, sender)
  }

  def work: Unit = {
    Thread.sleep(1000) // 실전에서는 절대 금물!!!
    log.info("Ping2 working...")
  }
}

class Ping3Actor extends Actor with ActorLogging {

  override def receive: Receive = {
    case Work =>
      log.info("Ping3 receive work")

      work
      sender().tell(Done, sender)
  }

  def work: Unit = {
    Thread.sleep(1000) // 실전에서는 절대 금물!!!
    log.info("Ping3 working...")
  }

}