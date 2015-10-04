// Working with lists

// Insertion sort

// The '::' in this example is a class(scala.::).
def isort(xs: List[Int]): List[Int] = xs match {
  case List() => List()
  case x :: xs1 => insert( x, isort(xs1) )
}
def insert(x: Int, xs: List[Int]): List[Int] = xs match {
  case List() => List(x)
  case y :: ys =>
    if (x <= y) x :: xs
    else y :: insert(x, ys)
}

println( isort( List(5, 4, 3, 2, 1) ) )


def append[T](xs: List[T], ys: List[T]): List[T] = xs match {
  case List() => ys
  case x :: xs1 => x :: append(xs1, ys)
}


// Merge sort

def msort[T](less: (T, T) => Boolean)(xs: List[T]): List[T] = {
  def merge(xs: List[T], ys: List[T]): List[T] =
    (xs, ys) match {
      case (Nil, _) => ys
      case (_, Nil) => xs
      case (x :: xs1, y :: ys1) =>
        if ( less(x, y) ) x :: merge(xs1, ys)
        else y :: merge(xs, ys1)
    }
  val n = xs.length / 2
  if (n == 0)
    xs
  else {
    val (ys, zs) = xs splitAt n
    merge( msort(less)(ys), msort(less)(zs) )
  }
}

val intSort = msort[Int](_ < _) _
println(  intSort( List(5, 4, 3, 2, 1) ) )


// Foldings

def sum(xs: List[Int]): Int = ( 0 /: xs )(_ + _)
def product(xs: List[Int]): Int = (1 /: xs)(_ * _)

println( sum(List(1,2,3)), product(List(4,5,6)) )

val words = List("the", "quick", "brown", "fox")
println( (words.head /: words.tail)(_ + " " + _) )
