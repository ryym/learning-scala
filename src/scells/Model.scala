package scells

// セルの入力と表示を分離するためのクラス。
// 例えば計算式が入力されたら、セルはその式自体ではなく
// 計算結果を表示しなければならない。
class Model(val height: Int, val width: Int)
  extends Evaluator with Arithmetic {

  case class Cell(row: Int, column: Int) {
    var formula: Formula = Empty
    def value = evaluate(formula)

    // 式の評価結果を文字列として返す。
    override def toString = formula match {
      case Textual(s) => s
      case _ => value.toString
    }

    def parse(input: String): Unit = {
      formula = FormulaParsers.parse(input)
    }
  }

  // new Array[Array[Cell]](height, width) is deprecated since 2.8
  val cells = Array.ofDim[Cell](height, width)

  for (i <- 0 until height; j <- 0 until width)
    cells(i)(j) = Cell(i, j)
}
