// Case classes and pattern matching

// An example of case class.
// A case class:
//  * can be instatiated without the 'new' keyword.
//  * has immutable fields automatically based on the parameter list.
//  * has the basic methods: `toString`, `hashCode`, and `equals` which
//    is implemented based on the parameter list.
abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr

val v = Var("x")
println(v, v.name)

val op = BinOp("+", Number(1), v)
println(op, op.left)

println(op.right == Var("x"))

// Pattern matching
//  The 'match' is an expression so it returns a value.
//  Unlike the 'switch' in Java, it doesn't evaluate next 'case'
//  even if there isn't a 'break'. And it throws an 'MatchError' exception
//  if no case is matched.

// An example
def simplifyTop(expr: Expr): Expr = expr match {
  case UnOp("-", UnOp("-", e))  => e  // -(-1) => 1
  case BinOp("+", e, Number(0)) => e  // 1 + 0 => 1
  case BinOp("+", e, Number(1)) => e  // 1 * 1 => 1
  case _ => expr
}
val expr = simplifyTop( UnOp("-", Number(10)) )

// Wild-card pattern matches any objects.
expr match {
  case BinOp(_, _, _) => println(expr + " is a binary operation")
  case _ => println("It's something else")
}

// Constant pattern matches itself.
def describe(x: Any) = x match {
  case 5       => "five"
  case true    => "truth"
  case "hello" => "hi!"
  case Nil     => "the empty list"
  case _       => "something else"
}

// Variable pattern matches any objects and bind it to the variable.
//  If the symbol starts with an lower case, Scala evaluates it as
//  an variable, not an cosntant.
import math.{E, Pi}

E match {
  case Pi => "strange math?"
  case _ => "OK"
} // => OK

E match {
  case pi => "strange math? Pi = " + pi
  // case _ => "OK" : <- This is unreachable code.
}

// Constructor pattern compares objects recursively in arbitrary depth.
expr match {
  case BinOp("+", e, Number(0)) => println("a deep match")
  case _ =>
}

// Tuple pattern can match a combination of different types.
def tupleDemo(expr: Any) =
  expr match {
    case (a, b, c) => println("matched" + a + b + c)
    case _ =>
  }
println( tupleDemo( ("a ", 3, "-tuple") ) )

// A pattern match with type.
def generalSize(x: Any) = x match {
  case s: String    => s.length
  case m: Map[_, _] => m.size
  case _ => -1
}

println( generalSize("abc") )
println( generalSize( Map(1 -> 'a', 2 -> 'b') ) )
println( generalSize(math.Pi) )

// Pattern guard

def matchWithGuards(x: Any): String = x match {
  case n: Int if 0 < n => "A positive number!"
  case s: String if s(0) == '+' => "A string which starts with '+'!"
  case BinOp("+", x, y) if x == y => "A binary operator whose lhs and rhs are same."
  case _ => "..."
}
