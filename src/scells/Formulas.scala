package scells

trait Formula

// A1, Z15, ..
case class Coord(row: Int, column: Int) extends Formula {
  override def toString = ('A' + column).toChar.toString + row
}

// C1:D10, ..
case class Range(c1: Coord, c2: Coord) extends Formula {
  override def toString = c1.toString + ":" + c2.toString
}

case class Number(value: Double) extends Formula {
  override def toString = value.toString
}

case class Textual(value: String) extends Formula {
  override def toString = value
}

// add(1, B15), ..
case class Application(
  function: String, arguments: List[Formula]
) extends Formula {
  override def toString = function + arguments.mkString("(", ",", ")")
}

object Empty extends Textual("")
