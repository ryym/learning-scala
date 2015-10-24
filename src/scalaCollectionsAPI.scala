// Scala collections API

import scala.collection.immutable._
import scala.collection.{mutable => mt}


// Initialize collections.

// The root trait of all collection classes.
// 'foreach' is the only abstract method of Traversable.
Traversable(1, 2, 3)

// A trait which extends Traversable.
Iterable("x", "y", "z")

Map("x" -> 24, "y" -> 25, "z" -> 26)
Set(-1, -2, -3.0)
SortedSet("hello", "world")

mt.Buffer( mt.Buffer(), mt.Buffer.empty )
IndexedSeq(1.0, 2.0)
LinearSeq(None, None, Some(1))

println( List(1, 2, 3) map (_ + 1) )
println( Set(4, 5, 6) + 7 )
println( mt.Set(4, 5, 6) + 7 )


// Map

val map = Map(1 -> "a", 2 -> "b")
println( map(1) )    // Throws an exception if the specified key doesn't exist.
println( map get 2 ) // Return Option value(Some(x) or None).

object MapMaker {
  import scala.collection.mutable.{Map, HashMap}
  def makeMap: Map[String, String] = {
    new HashMap[String, String] {
      override def default(key: String) = "Why do you want to know?"
    }
  }
}

val capital = MapMaker.makeMap
capital ++= List(
  "US"     -> "Washington",
  "France" -> "Paris",
  "Japan"  -> "Tokyo"
)
println( capital("Japan") )
println( capital("New Zealand") )
capital += ("New Zealand" -> "Wellington")
println( capital("New Zealand") )


// Stream
// We can create an infinite list using Stream
// which evaluates its elements lazily.

// '#::' is the 'cons' operator of Stream.
val stream = 1 #:: 2 #:: 3 #:: Stream.empty

// Fibonacci stream
// This doesn't cause infinite loop because
// the right-hand side of '#::' is evaluated lazily.
def fibFrom(a: Int, b: Int): Stream[Int] =
  a #:: fibFrom(b, a + b)

val fibStr = fibFrom(1, 1)
println( "Stream", fibStr(4) )
println( "Stream", fibStr.take(7).toList )


// Vector
// Vector provides random access and updates in effectively constant time.

val vec  = Vector.empty
val vec2 = vec :+ 1 :+ 2 :+ 3
println( "Vector", vec2(2) )
println( vec2 updated (2, 30) ) // Vector(1, 2, 30)


// immutable Stack and Queue

val stack = Stack.empty   // Stack[Nothing]
val hasOne = stack push 1 // Stack[Int]
println( stack, hasOne )

val q = Queue[Int]()
val has1 = q enqueue 1
val has123 = has1 enqueue List(2, 3)
val (first, has23) = has123.dequeue
println( has1, has123, first, has23 )


// Range

println( 1 to 3 )       // 1, 2, 3
println( 1 until 3 )    // 1, 2
println( 5 to 14 by 3 ) // 5, 8, 11, 14


// ArrayBuffer, ListBuffer

val abuf = mt.ArrayBuffer.empty[Int]
abuf += 1 += 10
println( abuf, abuf.toArray )

val lbuf = mt.ListBuffer.empty[Int]
lbuf += 2 += 3 -= 2
println( lbuf, lbuf.toList )


// Array

val array = Array(1, 2, 3, 4)
println( array map (_ * 3) mkString "," )
println( array filter (_ % 2 == 0) mkString "," )

// Initialize 'Array[T]' using ClassTag.
def evenElems[T: scala.reflect.ClassTag](xs: Vector[T]): Array[T] = {
  val arr = new Array[T]( (xs.length + 1) / 2 )
  for (i <- 0 until xs.length by 2)
    arr(i / 2) = xs(i)
  arr
}


// String
// We can use String like a collection.

val str = "Hello, World!"
println( str.reverse )
println( str map (_.toUpper) )
println( str drop 7 )
println( str slice (1, 6) )


// View
// Scala collections are by default 'strict' in all their
// transformers(map, filter, etc), except for Stream.
// View is a way to turn every collection into a non-strict or lazy one
// and vice versa.

import scala.collection.SeqView

// This transforms the vector twice, but it's a bit wasteful to construct
// the intermediate vector from the first call to 'map'.
val v = Vector(1 to 10: _*)
println( v )
println( v map (_ + 1) map (_ * 2) )

// But in View, two transformings are applied when 'force' method is called.
val vv: SeqView[Int, Vector[Int]] = v.view
println( vv.map(_ + 1).map(_ * 2).force )

// View provides a reference to the collection instead of copying values.
def negate(xs: mt.Seq[Int]) =
  for (i <- 0 until xs.length) xs(i) = -xs(i)
val arr = (0 to 9).toArray
val subarr = arr.view.slice(3, 6)
negate(subarr)
println( arr mkString "," ) // arr[3] to arr[6] are negated.


// Iterator
// Iterator also has 'foreach' method like Traversable, but when called to
// an iterator, 'foreach' will leave iterator at its end when it is done.

val it = Iterator("a", "number", "of", "words")
println( it.map(_.length) )

it foreach print _
println()
try {
  println( it.next() )
} catch {
  case ex: NoSuchElementException =>
    println("It throws an exception if 'next()' method is called when the iterator is at its end.")
}
