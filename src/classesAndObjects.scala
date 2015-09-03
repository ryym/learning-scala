// Composition and inheritance
// NOTE: Full implementation of Element classes are in '2dElements.scala'.


// The abstract class which represents an element to be displayed.
// An abstract method ('contents') doesn't need the 'abstract' prefix.
abstract class Element {
  def contents: Array[String]
  def height: Int = contents.length
  def width: Int  = if (height == 0) 0 else contents(0).length

  // Concat contents of the specified element.
  def above(that: Element): Element =
    new ArrayElement(this.contents ++ that.contents)

  // Concat each content line of the specified element to each this content line.
  def beside(that: Element): Element =
    new ArrayElement(
      for ( (line1, line2) <- this.contents zip that.contents )
        yield line1 + line2
    )

  override def toString = contents mkString "\n"
}

// Define the sub class of Element.
// This class *inheritances* Element, and is *composed* of Array[String].
class ArrayElement(conts: Array[String]) extends Element {
  def contents: Array[String] = conts
}

val ae = new ArrayElement( Array("abc") )
println( ae.width, ae.contents.length )
val e: Element = ae

// Using parameter-filed. This omits some redundant variables
// like 'conts' above.
class UniformElement(
  ch: Char,
  override val width: Int,
  override val height: Int
) extends Element {
  private val line = ch.toString * width
  def contents = Array.fill(height)(line)
}

// Super constructor can be called in 'extends'.
// Note: This class should inherit Element class directly
// because it is not so common to say LineElement 'is-a' ArrayElement.
// This is just an example.
class LineElement(s: String) extends ArrayElement( Array(s) ) {
  // The 'override' modifier is need, not optional when
  // overriding a concrete method of the super class.
  override def width  = s.length
  override def height = 1
}

// 'final' modifier is available.
final class UninheritableElement extends Element {
  def contents = Array()
}
class BlankElement extends Element {
  final def contents = Array("", "", "")
}
