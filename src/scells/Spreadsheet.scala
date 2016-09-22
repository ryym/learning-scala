package scells

import swing._
import event._

class Spreadsheet(val height: Int, val width: Int) extends ScrollPane {
  val table = new Table(height, width) {
    rowHeight = 25
    autoResizeMode = Table.AutoResizeMode.Off
    showGrid = true
    gridColor = new java.awt.Color(150, 150, 150)

    val cellModel = new Model(height, width)
    import cellModel._

    override def rendererComponent(
      isSelected: Boolean, hasFocus: Boolean,
      row: Int, column: Int
    ): Component = {
      if (hasFocus)
        new TextField(userData(row, column))
      else
        new Label(cells(row)(column).toString) {
          xAlignment = Alignment.Right
        }
    }

    def userData(row: Int, column: Int): String = {
      val v = this(row, column) // table.apply(row, column)
      if (v == null) "" else v.toString
    }

    reactions += {
      case TableUpdated(table, rows, column) =>
        for (row <- rows)
          cells(row)(column).parse(userData(row, column))

      // XXX: これ必要？ rendererComponent は毎回全てのセルに対して
      // 呼び出されるっぽいしなくてもいい気がする。
      case ValueChanged(cell) =>
        updateCell(cell.row, cell.column)
    }

    for (row <- cells; cell <- row) listenTo(cell)
  }

  val rowHeader = new ListView((0 until height) map (_.toString)) {
    fixedCellWidth = 30
    fixedCellHeight = table.rowHeight
  }

  viewportView = table
  rowHeaderView = rowHeader
}
