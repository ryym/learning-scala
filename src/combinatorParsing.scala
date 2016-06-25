import scala.util.parsing.combinator._
import java.io.FileReader

/*
 * expr   ::= term { "+" term | "-" term }.
 * term   ::= factor { "*" factor | "/" factor }.
 * factor ::= floatingPointNumber | "(" expr ")".
 */
object ArithParser extends JavaTokenParsers {
  // '~'s and '|'s are just methods.
  def expr: Parser[Any] = term~rep("+"~term | "-"~term)
  def term: Parser[Any] = factor~rep("*"~factor | "/"~factor)
  def factor: Parser[Any] = floatingPointNumber | "("~expr~")"

  def exec(input: String) {
    println("input: " + input)
    println(parseAll(expr, input))
  }
}

ArithParser.exec("2 * (3 + 7)")
ArithParser.exec("2 * (3 + 7))")

/*
 * value   ::= obj | arr | stringLiteral |
 *           floatingPointNumber |
 *           "null" | "true" | "false".
 * obj     ::= "{" [mmembers] "}".
 * arr     ::= "[" [values] "]".
 * members ::= member { "," member }.
 * member  ::= stringLiteral ":" value.
 * values  ::= value { "," value }.
 *
 * JSONをScalaで扱いやすいデータ構造に変換する。
 */
object JSONParser extends JavaTokenParsers {
  def value: Parser[Any] =
    obj | arr | stringLiteral |
    floatingPointNumber |
    "null" | "true" | "false"

  // '~>'は左の, '<~'は右の解析結果を捨てる
  def obj: Parser[Map[String, Any]] =
    "{"~> repsep(member, ",") <~"}" ^^
    (Map() ++ _)

  def arr: Parser[List[Any]] =
    "["~> repsep(value, ",") <~"]"

  // 結果変換用のcaseの値に注目。Parserを組み立てるための
  // '~'は親クラスのメソッドだが、マッチに使われているのは
  // '~'という名のケースクラス。名前に記号しか含まないケースクラスは、
  // メソッドと同じように中置できるためこういう書き方ができる
  // (つまり下記は'~(~(name, ":")), value)'と同様)。
  // しかしこういう書き方ができるのはパターンマッチ内のみっぽい。
  // 'println("" ~ "")とかすると構文エラーになる。
  def member: Parser[(String, Any)] =
    stringLiteral~":"~value ^^
    { case name~":"~value => (name, value) }


  def exec(path: String) {
    val reader = new FileReader(path)

    // The 'parseAll' method is overloaded.
    println(parseAll(value, reader))
  }
}

JSONParser.exec("address-book.json")
