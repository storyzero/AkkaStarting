package chapter6

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.RoundRobinPool

/**
  * Created by storyzero on 2016. 5. 12..
  */
object Main extends App {
  val system = ActorSystem("TestSystem")
  val ping = system.actorOf(Props[PingActor], "pingActor")
  ping.tell(Start, ActorRef.noSender)
}

object Start

class PingActor extends Actor with ActorLogging {

  import context._

  val childRouter = actorOf(RoundRobinPool(5).props(Props[Ping1Actor]), "ping1Actor")
//  val childRouter = actorOf(Props[Ping1Actor], "ping1Actor")

  override def receive: Receive = {
    case Start =>
      1 to 10 foreach { i => childRouter.tell(i, self) }
      log.info("PingActor sent 10 messages to the router")
    case a @ _  =>
      unhandled(a)
  }
}

class Ping1Actor extends Actor with ActorLogging {

  override def receive: Receive = {
    case msg: Int =>
      log.info("Ping1Actor({}) received {}", hashCode, msg)
      work(msg)
  }

  def work(n: Int) = {
    log.info("Ping1Actor({}) working on {}", hashCode, n)
    Thread.sleep(1000) // 실전에서는 절대 금물!!!
    log.info("Ping1Actor({}) completed ", hashCode)
  }
}
