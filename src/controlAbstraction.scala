// Control Abstraction

// Pass a function as a parameter. {{{1

// Implement APIs by higher-order functions.
object FileMatcher {
  private def filesHere = (new java.io.File(".")).listFiles

  // 'matcher' is a function that accepts a string and returns a boolean value.
  private def filesMatching(matcher: String => Boolean) =
    for (file <- filesHere; if matcher(file.getName))
      yield file

  def filesEnding(query: String) = filesMatching( _.endsWith(query) )
  def filesContaining(query: String) = filesMatching( _.contains(query) )
  def filesRegex(query: String) = filesMatching( _.matches(query) )
}

// Using APIs that accepts a higher-order function.
def containsNeg(nums: List[Int]) = nums.exists(_ < 0)
def containsOdd(nums: List[Int]) = nums.exists(_ % 2 == 1)

// If we implement the above examples without higher-order functions..
def containsNeg2(nums: List[Int]) = {
  // (If we use 'return' statement, result type must be defined).
  var exists = false
  for (num <- nums)
    if (num < 0)
      exists = true
  exists
}

println( containsNeg( List(1,2,3) ) )
println( containsNeg2( List(1,2,3) ) )

// Curried functions {{{1

// Define a curried function.
def curriedSum(x: Int)(y: Int) = x + y

println( curriedSum(1)(2) )

val threePlus = curriedSum(3)_
println( threePlus(1), threePlus(3) )

// Define new structures {{{1

// Execute the specified function twice.
def twice(op: Int => Int, arg: Int) = op( op(arg) )
println( twice(_ + 1, 5) ) // => 7
println( twice(threePlus, 10) ) // => 16

import java.io.File
import java.io.PrintWriter

// Define the function which 'loan's a print-writer.
def withPrintWriterSimple(file: File, op: PrintWriter => Unit) {
  val writer = new PrintWriter(file)
  try {
    op(writer)
  } finally {
    writer.close()
  }
}

// It can be used as:
withPrintWriterSimple(
  new File("src/data.txt"),
  writer => writer.println("written!")
)

// Using cariied-function and braces, the above example
// can be written like a built-in structure.
def withPrintWriter(file: File)(op: PrintWriter => Unit) {
  val writer = new PrintWriter(file)
  try {
    op(writer)
  } finally {
    writer.close()
  }
}

val dataFile = new File("src/data.txt")
withPrintWriter(dataFile) {
  writer => writer.println(new java.util.Date)
}
