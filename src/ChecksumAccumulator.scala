// Classes and objects

import scala.collection.mutable.Map

// Companion class
class ChecksumAccumulator {
  private var sum = 0
  def add(b: Byte) { sum += b } // Procedure format
  def checksum(): Int = ~(sum & 0xFF) + 1
}

// Companion object
// Note: In Scala, 'static' member doesn't exist.
object ChecksumAccumulator {
  private val cache = Map[String, Int]()
  def calculate(s: String): Int =
    if (cache.contains(s))
      cache(s)
    else {
      val acc = new ChecksumAccumulator
      for (c <- s)
        acc.add(c.toByte)
      val cs = acc.checksum()
      cache += (s -> cs)
      cs
    }
}

println( ChecksumAccumulator.calculate("Every value is an object.") )
