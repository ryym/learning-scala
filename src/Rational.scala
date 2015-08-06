
class Rational(n: Int, d: Int) {
  require(d != 0) // Defined in Predef object

  // Fields must be defined explicitly.
  private val g = gcd(n.abs, d.abs)
  val numer: Int = n / g
  val denom: Int = d / g

  // Auxiliary constructor must call another constructor.
  def this(n: Int) = this(n, 1)

  def add(that: Rational): Rational = 
    new Rational(
      numer * that.denom + that.numer * denom,
      denom * that.denom
    )

  def lessThan(that: Rational) =
    this.numer * this.denom < that.numer * that.denom

  def max(that: Rational) = if (this.lessThan(that)) that else this

  override def toString = numer + "/" + denom

  private def gcd(a: Int, b: Int): Int =
    if (b == 0) a else gcd(b, a % b)
}
