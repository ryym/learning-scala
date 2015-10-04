// Abstract Members


// Lazy val
trait LazyRationalTrait {
  val numerArg: Int
  val denomArg: Int
  lazy val numer = numerArg / g
  lazy val denom = denomArg / g

  override def toString = numer + "/" + denom

  private lazy val g = {
    require(denomArg != 0)
    gcd(numerArg, denomArg)
  }

  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)
}

val x = 2
val half = new LazyRationalTrait {
  val numerArg = 1 * x
  val denomArg = 2 * x
}

// Evaluation order:
// 1. toString
// 2. numer
// 3. g
// 4. denom
println(half)


// Abstract type

class Food
class Grass extends Food
class DogFood extends Food

abstract class Animal {
  // Abstract type
  type SuitableFood <: Food
  def eat(food: SuitableFood) {}
}

class Cow extends Animal {
  // Define my suitable food.
  type SuitableFood = Grass
}

class Dog extends Animal {
  type SuitableFood = DogFood
}

val bessy: Cow = new Cow
bessy eat new Grass
bessy eat (new bessy.SuitableFood)

val lassie = new Dog
val bootsie = new Dog
lassie eat new DogFood
lassie eat new lassie.SuitableFood
lassie eat new bootsie.SuitableFood


// XXX: What is the defference between the abstract type pattern
// and the following Java-like pattern?
// Both patterns can be compiled.
abstract class Animal2[S <: Food] {
  def eat(food: S)
}
class Cow2 extends Animal2[Grass] {
  override def eat(food: Grass) {}
}


// Case study - Currency

abstract class CurrencyZone {
  type Currency <: AbstractCurrency
  def make(x: Long): Currency
  val CurrencyUnit: Currency

  abstract class AbstractCurrency {
    val amount: Long
    def designation: String

    def + (that: Currency) = make(this.amount + that.amount)
    def - (that: Currency) = make(this.amount - that.amount)
    def * (x: Double)      = make( (this.amount * x).toLong )
    def / (x: Double)      = make( (this.amount / x).toLong )
    def / (that: Currency) = this.amount.toDouble / that.amount

    def from(other: CurrencyZone#AbstractCurrency): Currency = {
      val rate = Converter.exchangeRate(other.designation)(this.designation)
      make(math.round( other.amount.toDouble * rate ))
    }

    override def toString = (
      (amount.toDouble / CurrencyUnit.amount.toDouble)
      formatted ("%." + decimals(CurrencyUnit.amount) + "f")
      + " " + designation
    )

    private def decimals(n: Long): Int =
      if (n == 1) 0 else 1 + decimals(n / 10)
  }
}

object Converter {
  // Map[String, Map[String, Double]]
  var exchangeRate = Map(
    "USD" -> Map(
      "USD" -> 1.0,
      "EUR" -> 0.7596,
      "JPY" -> 1.211,
      "CHF" -> 1.223
    ),
    "EUR" -> Map(
      "USD" -> 1.316,
      "EUR" -> 1.0,
      "JPY" -> 1.594,
      "CHF" -> 1.623
    ),
    "JPY" -> Map(
      "USD" -> 0.8257,
      "EUR" -> 0.6272,
      "JPY" -> 1.0,
      "CHF" -> 1.018
    ),
    "CHF" -> Map(
      "USD" -> 0.8108,
      "EUR" -> 0.8108,
      "JPY" -> 0.982,
      "CHF" -> 1.0
    )
  )
}

object US extends CurrencyZone {
  abstract class Dollar extends AbstractCurrency {
    def designation = "USD" // Define designation.
  }

  type Currency = Dollar

  def make(cents: Long) = new Dollar {
    val amount = cents // Define amount.
  }

  val Cent   = make(1)
  val Dollar = make(100)
  val CurrencyUnit = Dollar
}

object Europe extends CurrencyZone {
  abstract class Euro extends AbstractCurrency {
    def designation = "EUR"
  }

  type Currency = Euro

  def make(cents: Long) = new Euro {
    val amount = cents
  }

  val Cent = make(1)
  val Euro = make(100)
  val CurrencyUnit = Euro
}

object Japan extends CurrencyZone {
  abstract class Yen extends AbstractCurrency {
    def designation = "JPY"
  }

  type Currency = Yen

  def make(yen: Long) = new Yen {
    val amount = yen
  }

  val Yen = make(1)
  val CurrencyUnit = Yen
}

println( Japan.Yen from US.Dollar * 100 )
println( Europe.Euro from US.Dollar * 200 )

val taroYen = Japan.make(1000)
val hanaYen = Japan.make(3000)
println( taroYen + hanaYen )
println( hanaYen / taroYen )
println( taroYen from hanaYen ) // Nonsense
