package scells

import swing.{event, Publisher}

// セルの入力と表示を分離するためのクラス。
// 例えば計算式が入力されたら、セルはその式自体ではなく
// 計算結果を表示しなければならない。
class Model(val height: Int, val width: Int)
  extends Evaluator with Arithmetic {

  case class ValueChanged(cell: Cell) extends event.Event

  case class Cell(row: Int, column: Int) extends Publisher {
    private var f: Formula = Empty
    def formula: Formula = f
    def formula_=(newFormula: Formula) {
      f = newFormula
      for (c <- references(formula)) deafTo(c)
      for (c <- references(newFormula)) listenTo(c)
      value = evaluate(newFormula)
    }

    private var v: Double = 0
    def value: Double = v
    def value_=(w: Double) {
      if (! (v == w || v.isNaN && w.isNaN)) {
        v = w
        publish(ValueChanged(this))
      }
    }

    // 式の評価結果を文字列として返す。
    override def toString = formula match {
      case Textual(s) => s
      case _ => value.toString
    }

    def parse(input: String): Unit = {
      formula = FormulaParsers.parse(input)
    }

    reactions += {
      case ValueChanged(_) => value = evaluate(formula)
    }
  }

  // new Array[Array[Cell]](height, width) is deprecated since 2.8
  val cells = Array.ofDim[Cell](height, width)

  for (i <- 0 until height; j <- 0 until width)
    cells(i)(j) = Cell(i, j)
}
