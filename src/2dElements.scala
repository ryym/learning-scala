import Element.elem

abstract class Element {
  def contents: Array[String]
  def width: Int  = if (height == 0) 0 else contents(0).length
  def height: Int = contents.length

  // Put this element above the other.
  def above(that: Element): Element = {
    val this1 = this widen that.width
    val that1 = that widen this.width
    elem(this1.contents ++ that1.contents)
  }

  // Put this element beside the other.
  def beside(that: Element): Element = {
    val this1 = this heighten that.height
    val that1 = that heighten this.height
    elem(
      for ( (line1, line2) <- this1.contents zip that1.contents )
        yield line1 + line2
      )
  }

  // Adjust this element's width.
  def widen(w: Int): Element =
    if (w <= width) this
    else {
      val wdiff = w - width
      val left  = elem(' ', wdiff / 2, height)
      val right = elem(' ', wdiff - left.width, height)
      left beside this beside right
    }

  // Adjust this element's height.
  def heighten(h: Int): Element =
    if (h <= height) this
    else {
      val hdiff = h - height
      val top = elem(' ', width, hdiff / 2)
      val bot = elem(' ', width, hdiff - top.height)
      top above this above bot
    }

  override def toString = contents mkString "\n"
}

object Element {
  private class ArrayElement(
    val contents: Array[String]
  ) extends Element

  private class LineElement(s: String) extends Element {
    val contents = Array(s)
    override def width  = s.length
    override def height = 1
  }

  private class UniformElement(
    chr: Char,
    override val width: Int,
    override val height: Int
  ) extends Element {
    private val line = chr.toString * width
    def contents = Array.fill(height)(line)
  }

  def elem(contents: Array[String]): Element =
    new ArrayElement(contents)

  def elem(chr: Char, width: Int, height: Int): Element =
    new UniformElement(chr, width, height)
  
  def elem(line: String): Element =
    new LineElement(line)
}

// The class which draws spiral lines.
object Spiral {
  val space  = elem(" ")
  val corner = elem("+")

  def main(args: Array[String]) {
    val nSides = args(0).toInt
    println( spiral(nSides, 0) )
  }

  def spiral(nEdges: Int, direction: Int): Element = {
    if (nEdges == 1)
      corner
    else {
      val sp = spiral(nEdges - 1, (direction + 3) % 4)
      def verticalBar   = elem('|', 1, sp.height)
      def horizontalBar = elem('-', sp.width, 1)

      if (direction == 0)
        (corner beside horizontalBar) above (sp beside space)
      else if (direction == 1)
        (sp above space) beside (corner above verticalBar)
      else if (direction == 2)
        (space beside sp) above (horizontalBar beside corner)
      else
        (verticalBar above corner) beside (space above sp)
      /*
       *      up    r     down  l
       * dir: 0     1     2     3
       *      + -   sp+   ^sp   | ^
       *      sp^   ^ |   - +   +sp
       */
    }
  }
}

// (edges, dir): (3, 0), (2, 3), (1, 2)
Spiral.main( Array("3") )

// (edges, dir): (4, 0), (3, 3), (2, 2), (1, 1)
Spiral.main( Array("4") )

// (edges, dir): (6, 0), (5, 3), (4, 2), (3, 1), (2, 0), (1, 3)
Spiral.main( Array("6") )
