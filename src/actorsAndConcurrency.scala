/* Actors and Concurrency */

// Note that the scala.actors package is deprecated in Scala 2.11.
// We should use the akka.actor package instead.
// (http://docs.scala-lang.org/overviews/core/actors-migration-guide.html)
import scala.actors.Actor
import Actor._

// Actorパターンでは、メッセージパッシングのみを介して非同期処理を行う。
// メッセージはどんな型のものでも送信できるためAny型だが、受け取る方は
// 部分関数でパターンマッチングできるので、わざわざ型変換する必要などは無いし、
// 必要に応じていくらでも取捨選択できる。

// Actors created by the actor method starts immediately.
// We can wait more messages using `while(true)`.
val echoActor = actor {
  // scala.actors.Actor.receive
  receive {
    case msg =>
      println("received message: " + msg)
  }
}
echoActor ! "hi there"

object NameResolver extends Actor {
  import java.net.{InetAddress, UnknownHostException}

  def act() {
    loop {
      react {
        case (name: String, actor: Actor) =>
          actor ! getIp(name)
        case "EXIT" =>
          exit()
        case msg =>
          println("Unhandled message: " + msg)
      }
    }
  }

  def getIp(name: String): Option[InetAddress] = {
    try {
      Some(InetAddress.getByName(name))
    } catch {
      case _: UnknownHostException => None
    }
  }
}

NameResolver.start()
NameResolver ! ("www.scala-lang.org", self)

self.receiveWithin(1000) {
  case x =>
    println(x)
    NameResolver ! "EXIT"
}

// Actor should not block processes.
val nonBlockingActor = actor {
  val mainActor = self
  var emoted = 0

  def emoteLater() = actor {
    Thread.sleep(100)
    mainActor ! "Emote"
  }

  emoteLater()

  loop {
    react {
      case "Emote" =>
        println("I'm acting!")
        emoted += 1
        if (emoted < 5)
          emoteLater()
        else
          exit()
    }
  }
}

/*
 * Re-implement the circuit simulation implemented at chapter 18 (statefull objects).
 */

// The messages used between Clock and Circuit.
case class Ping(time: Int)
case class Pong(time: Int, from: Actor)
case class AfterDelay(delay: Int, msg: Any, target: Actor)
case object Start
case object Stop

/**
 * Clockは回路シミュレーションのスケジュールを管理する。
 * timeを1つずつ進めていき、そのタイミングで実行されるよう登録されている
 * アクターに実行命令を送った後でPingメッセージを送る。
 * 全てのアクターがPongを返した時点で次のtimeを進める。
 * ただこの例では、シミュレーション開始時にPong前にセットアップを行うくらいで、
 * 以降はPingを受け取ったらすぐにPongを返している。要は各Simulantの同期に必要?
 */
class Clock extends Actor {
  private case class WorkItem(time: Int, msg: Any, target: Actor)

  private var running = false
  private var currentTime = 0
  private var agenda: List[WorkItem] = List()
  private var allSimulants: List[Actor] = List()
  private var busySimulants: Set[Actor] = Set.empty

  def add(sim: Simulant) {
    allSimulants = sim :: allSimulants
  }

  def act() {
    loop {
      if (running && busySimulants.isEmpty)
        advance()
      reactToOneMessage()
    }
  }

  private def advance() {
    if (agenda.isEmpty && currentTime > 0) {
      println("** Agenda empty. Clock exiting at time " + currentTime + ".")
      self ! Stop
      return
    }

    currentTime += 1
    println("Advancing to time " + currentTime)

    processCurrentEvents()
    for (sim <- allSimulants) {
      sim ! Ping(currentTime)
    }

    busySimulants = Set.empty ++ allSimulants
  }

  private def processCurrentEvents() {
    val todoNow = agenda.takeWhile(_.time == currentTime)
    agenda = agenda.drop(todoNow.length)
    for (WorkItem(time, msg, target) <- todoNow) {
      target ! msg
    }
  }

  private def reactToOneMessage() {
    react {
      case Start =>
        running = true

      case Stop =>
        allSimulants.foreach(_ ! Stop)
        exit()

      case AfterDelay(delay, msg, target) =>
        val item = WorkItem(currentTime + delay, msg, target)
        agenda = insert(agenda, item)

      case Pong(time, sim) =>
        assert(time == currentTime)
        assert(busySimulants contains sim)
        busySimulants -= sim
    }
  }

  private def insert(ag: List[WorkItem], item: WorkItem): List[WorkItem] =
    if (ag.isEmpty || item.time < ag.head.time)
      item :: ag
    else
      ag.head :: insert(ag.tail, item)
}

// シミュレート対象となるオブジェクト用trait
trait Simulant extends Actor {
  val clock: Clock  // Abstract val

  def handleSimMessage(msg: Any)

  def simStarting() {}

  override def act() {
    loop {
      react {
        case Ping(time) =>
          if (time == 1) {
            simStarting()
          }
          clock ! Pong(time, self)
        case Stop => exit()
        case msg => handleSimMessage(msg)
      }
    }
  }

  // Start when created.
  start()
}

class Circuit {
  val clock = new Clock

  clock.start()

  case class SetSignal(sig: Boolean)
  case class SignalChanged(wire: Wire, sig: Boolean)

  val WireDelay = 1
  val InverterDelay = 2
  val OrGateDelay = 3
  val AndGateDelay = 3

  class Wire(
    name: String = "unnamed",
    init: Boolean = false
  ) extends Simulant {
    val clock = Circuit.this.clock
    private var sigVal = init
    private var observers: List[Actor] = List()

    clock.add(this)

    override def handleSimMessage(msg: Any) {
      msg match {
        case SetSignal(s) =>
          if (s != sigVal) {
            sigVal = s
            signalObservers()
          }
      }
    }
    
    override def simStarting() {
      signalObservers()
    }

    def addObserver(obs: Actor) {
      observers = obs :: observers
    }

    private def signalObservers() {
      for (obs <- observers) {
        clock ! AfterDelay(WireDelay, SignalChanged(this, sigVal), obs)
      }
    }

    override def toString = "Wire(" + name + ")"
  }

  abstract class Gate(in1: Wire, in2: Wire, out: Wire) extends Simulant {
    val clock = Circuit.this.clock
    val delay: Int
    private var s1, s2 = false

    clock.add(this)
    in1.addObserver(this)
    in2.addObserver(this)

    def computeOutput(s1: Boolean, s2: Boolean): Boolean

    override def handleSimMessage(msg: Any) {
      msg match {
        case SignalChanged(w, sig) =>
          if (w == in1)
            s1 = sig
          if (w == in2)
            s2 = sig

          val output = computeOutput(s1, s2)
          clock ! AfterDelay(delay, SetSignal(output), out)
      }
    }
  }

  def start() {
    clock ! Start
  }

  // Simulant objects (factories)

  private object DummyWire extends Wire("dummy")

  def orGate(in1: Wire, in2: Wire, out: Wire) =
    new Gate(in1, in2, out) {
      val delay = OrGateDelay
      override def computeOutput(s1: Boolean, s2: Boolean) = s1 || s2
    }

  def andGate(in1: Wire, in2: Wire, out: Wire) =
    new Gate(in1, in2, out) {
      val delay = AndGateDelay
      override def computeOutput(s1: Boolean, s2: Boolean) = s1 && s2
    }

  def inverter(in: Wire, out: Wire) =
    new Gate(in, DummyWire, out) {
      val delay = InverterDelay
      override def computeOutput(s1: Boolean, ignored: Boolean) = !s1
    }

  def probe(wire: Wire) = new Simulant {
    val clock = Circuit.this.clock

    clock.add(this)
    wire.addObserver(this)

    def handleSimMessage(msg: Any) {
      msg match {
        case SignalChanged(wire, sig) =>
          println("signal " + wire + " changed to " + sig)
      }
    }
  }

  def probeAll(wires: Wire*) {
    wires.foreach(probe(_))
  }
}

trait Adders extends Circuit {
  def halfAdder(a: Wire, b: Wire, s: Wire, c: Wire) {
    val d, e = new Wire
    orGate(a, b, d)
    andGate(a, b, c)
    inverter(c, e)
    andGate(d, e, s)
  }

  def fullAdder(
    a: Wire, b: Wire, cin: Wire,
    sum: Wire, cout: Wire
  ) {
    val s, c1, c2 = new Wire
    halfAdder(a, cin, s, c1)
    halfAdder(b, s, sum, c2)
    orGate(c1, c2, cout)
  }
}

object Demo {
  def main(args: Array[String]) {
    val circuit = new Circuit with Adders
    import circuit._

    // 1, 0, 1
    val ain = new Wire("ain", true)
    val bin = new Wire("bin", false)
    val cin = new Wire("cin", true)

    val sout = new Wire("sout")
    val cout = new Wire("cout")

    probeAll(ain, bin, cin, sout, cout)
    fullAdder(ain, bin, cin, sout, cout)
    circuit.start()
  }
}

Thread.sleep(1200)
Demo.main(Array())
