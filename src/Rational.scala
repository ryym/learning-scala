
class Rational(n: Int, d: Int) extends Ordered[Rational] {
  require(d != 0) // Defined in Predef object

  // Fields must be defined explicitly.
  private val g = gcd(n.abs, d.abs)
  val numer: Int = n / g
  val denom: Int = d / g

  // Auxiliary constructor must call another constructor.
  def this(n: Int) = this(n, 1)

  def + (that: Rational): Rational =
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )
  def + (i: Int): Rational =
    new Rational(numer + i * denom, denom)

  def - (that: Rational): Rational =
    new Rational(
      numer * that.denom - that.numer * denom,
      denom * that.denom
    )
  def - (i: Int): Rational =
    new Rational(numer - i * denom, denom)

  def * (that: Rational): Rational =
    new Rational(numer * that.numer, denom * that.denom)
  def * (i: Int): Rational =
    new Rational(numer * i, denom)

  def / (that: Rational): Rational =
    new Rational(numer * that.denom, denom * that.numer)
  def / (i: Int): Rational =
    new Rational(numer, denom * i)

  def lessThan(that: Rational) =
    this.numer * this.denom < that.numer * that.denom

  def max(that: Rational) = if (this.lessThan(that)) that else this

  // By this one method, Rational class can provide all comparing methods.
  def compare(that: Rational) =
    (this.numer * that.denom) - (that.numer * this.denom)

  override def toString = numer + "/" + denom

  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)
}

// Implicit conversions (=> chapter 21)
// implicit def intToRational(x: Int) = new Rational(x)

val half = new Rational(1, 2)
val third = new Rational(1, 3)
println( half < third )
println( half > third )
