// Extractors

// Extractor is a way to write readable pattern matching without case classes.

object EMail {
  // Injector (optional)
  def apply(user: String, domain: String) =user + "@" + domain

  // Extractor(required)
  def unapply(str: String): Option[(String, String)] = {
    val parts = str split "@"
    if (parts.length == 2) Some(parts(0), parts(1)) else None
  }
}

// `String` is not a case class, but we can do pattern matching by Extractor.
// The case matches if `EMail.unapply(email)` returns `Some(user, domain)`.
"user@domain.com" match {
  case EMail(user, domain) => println("User is '" + user + "', domain is '" + domain + "'")
  case _ => println("Not an email address")
}
val EMail(user, domain) = "yamamoto@yahoo.com"
println(user, domain)

// Return one result.
object Twice {
  def apply(s: String) = s + s

  def unapply(s: String): Option[String] = {
    val length = s.length / 2
    val half = s.substring(0, length)
    if (half == s.substring(length)) Some(half) else None
  }
}

// Bind no variables.
object UpperCase {
  def unapply(s: String): Boolean = s.toUpperCase == s
}

def userTwiceUpper(s: String) = s match {
  // '@' binds a value to 'x' which matched to `UpperCase()`.
  case EMail( Twice(x @ UpperCase()), domain ) =>
    "match: " + x + " in domain " + domain
  case _ =>
    "no match"
}
println( userTwiceUpper("DIDI@hotmail.com") )
println( userTwiceUpper("DIDO@hotmail.com") )
println( userTwiceUpper("didi@hotmail.com") )


// Take variable length arguments.
object Domain {
  def apply(parts: String*): String = parts.reverse.mkString(".")

  def unapplySeq(whole: String): Option[Seq[String]] =
    Some( whole.split("\\.").reverse )
}

"java.sum.com" match {
  case Domain("org", "acm") => println("Hi, acm!")
  case Domain("com", "sum", "java") => println("Java, yay!")
  case Domain("net", _*) => println("a .net domain")
}

def isTomInDotCom(s: String): Boolean = s match {
  case EMail("tom", Domain("com", _*)) => true
  case _ => false
}
println( isTomInDotCom("tom@sum.com") )
println( isTomInDotCom("peter@sum.com") )
println( isTomInDotCom("tom@sum.org") )

object ExpandedEMail {
  def unapplySeq(email: String): Option[(String, Seq[String])] = {
    val parts = email split "@"
    if (parts.length == 2)
      Some(parts(0), parts(1).split("\\.").reverse)
    else
      None
  }
}

val tomsAddress = "tom@support.epfl.ch"
val ExpandedEMail(name, topDomain, subDomains @ _*) = tomsAddress
println(name, topDomain, subDomains)
var ExpandedEMail(tom, topDom, secondDom, otherDoms @ _*) = tomsAddress
println(tom, topDom, secondDom, otherDoms)


// Regular expression

// import scala.util.matching.Regex
// val Decimal = new Regex("(-)?(\\d+)(\\.\\d*)?")
// val Decimal = new Regex("""(-)?(\d+)(\.\d*)?""")
val Decimal = """(-)?(\d+)(\.\d*)?""".r

val input = "for -1.0 to 99 by 3"
println( Decimal findFirstIn input )
for (s <- Decimal findAllIn input)
  println(s)

// Extract mached strings using Extractor.
var Decimal(sign, integerpart, decimalpart) = "-1.23"
println( sign, integerpart, decimalpart )

for (Decimal(s, i, d) <- Decimal findAllIn "-1.23, -4, 1.02, 3")
  println("sign: " + s, "integer: " + i, "decimal: " + d)
