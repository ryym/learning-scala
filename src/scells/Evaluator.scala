package scells

trait Evaluator { this: Model =>
  type Op = List[Double] => Double

  // 拡張可能にするため mutable なマップを使う。
  val operations = new collection.mutable.HashMap[String, Op]

  def evaluate(e: Formula): Double = try {
    e match {
      case Coord(row, column) =>
        cells(row)(column).value
      case Number(v) =>
        v
      case Textual(_) =>
        0
      case Application(function, arguments) =>
        val argvals = arguments flatMap evalList
        operations(function)(argvals)
    }
  } catch {
    case ex: Exception => Double.NaN
  }

  private def evalList(e: Formula): List[Double] = e match {
    // セル範囲の場合は範囲内のセルの各値をリストにして返す。
    // case Range(_, _) => references(e) map (_.value)
    case range: Range => references(range) map (_.value)
    case _ => List(evaluate(e))
  }

  def references(e: Formula): List[Cell] = e match {
    case Coord(row, column) =>
      List(cells(row)(column))
    case Range(Coord(r1, c1), Coord(r2, c2)) =>
      // yield の戻り値をIndexedSeqでなくListにするため、toListを呼ぶ。
      for (row <- (r1 to r2).toList; column <- c1 to c2)
        yield this.cells(row)(column)
    case Application(function, arguments) =>
      arguments flatMap references
    case _ =>
      List()
  }
}

trait Arithmetic { this: Evaluator =>
  operations += (
    "add" -> { case List(x, y) => x + y },
    "sub" -> { case List(x, y) => x - y },
    "div" -> { case List(x, y) => x / y },
    "mul" -> { case List(x, y) => x * y },
    "mod" -> { case List(x, y) => x % y },

    // /: is foldLeft (same as xs.foldLeft(0.0)(_ + _))
    "sum" -> { xs => (0.0 /: xs)(_ + _) },
    "prod" -> { xs => (1.0 /: xs)(_ * _) }
  )
}
