// Implicit Parameters

class PreferredPrompt(val preference: String)
class PreferredDrink(val preference: String)

object Greeter {
  def greet(name: String)
           (implicit prompt: PreferredPrompt, drink: PreferredDrink) {
    println("Welcome, " + name + ". The system is ready.")
    println("But while you work, ")
    println("why not enjoy a cup of " + drink.preference + "?")
    println(prompt.preference)
  }
}

val bobsPrompt = new PreferredPrompt("relax> ")
val bobsDrink  = new PreferredDrink("milk")
Greeter.greet("Bob")(bobsPrompt, bobsDrink)

object JoesPrefs {
  // `implicit` vals.
  implicit val prompt = new PreferredPrompt("Yes, master> ")
  implicit val drink  = new PreferredDrink("tea")
}

// Pass Joe's preferences implicitly.
import JoesPrefs._
Greeter.greet("Joe")


// This method can't take `List[Int]` because `Int` doesn't inherit `Ordered[T]`.
def maxListUpBound[T <: Ordered[T]](elements: List[T]): T =
  elements match {
    case List()    => throw new IllegalArgumentException("empty list!")
    case List(x)   => x
    case x :: rest =>
      val maxRest = maxListUpBound(rest)
      if (x > maxRest) x else maxRest
  }

// Use explicit converter to convert `T` to `Ordered[T]`.
def maxListExpParam[T](elements: List[T])(orderer: T => Ordered[T]): T =
  elements match {
    case List()    => throw new IllegalArgumentException("empty list!")
    case List(x)   => x
    case x :: rest =>
      val maxRest = maxListImpParam(rest)(orderer)
      if (orderer(x) > maxRest) x else maxRest
  }

// Use an implicit parameter.
def maxListImpParam[T](elements: List[T])(implicit orderer: T => Ordered[T]): T =
  elements match {
    case List()    => throw new IllegalArgumentException("empty list!")
    case List(x)   => x
    case x :: rest =>
      val maxRest = maxListImpParam(rest)
      if (x > maxRest) x else maxRest
  }

// Use view bound.
def maxList[T <% Ordered[T]](elements: List[T]): T =
  elements match {
    case List()    => throw new IllegalArgumentException("empty list!")
    case List(x)   => x
    case x :: rest =>
      val maxRest = maxList(rest)
      if (x > maxRest) x else maxRest
  }
