package scells

import scala.util.parsing.combinator._

object FormulaParsers extends RegexParsers {
  def ident: Parser[String] = """[a-zA-Z_]\w*""".r

  def decimal: Parser[String] = """-?\d+(\.\d*)?""".r

  // 現状、アルファベットは1字までしか対応していない。
  def cell: Parser[Coord] =
    """[A-Za-z]\d+""".r ^^ { s =>
      val column = s.charAt(0).toUpper - 'A'
      val row = s.substring(1).toInt
      Coord(row, column)
    }

  def range: Parser[Range] =
    cell~":"~cell ^^ {
      case c1~":"~c2 => Range(c1, c2)
    }

  def number: Parser[Number] =
    decimal ^^ (d => Number(d.toDouble))

  def textual: Parser[Textual] =
    """[^=].*""".r ^^ Textual

  def application: Parser[Application] =
    ident~"("~repsep(expr, ",")~")" ^^ {
      case f~"("~ps~")" => Application(f, ps)
    }

  // 本来なら、range はトップレベルの式としては無効な値。
  def expr: Parser[Formula] =
    range | cell | number | application

  def formula: Parser[Formula] =
    number | textual | "="~>expr


  def parse(input: String): Formula =
    parseAll(formula, input) match {
      case Success(e, _) => e
      case f: NoSuccess => Textual("[" + f.msg + "]")
    }
}
