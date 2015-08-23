// Builtin structures
// vim: foldmethod=marker

/* If {{{1 */

val filename = if (! args.isEmpty) args(0) else "default.txt"
println(filename)

/* while {{{1 */

// 'while' is a statement, not a expression.
def gcdLoop(x: Long, y: Long) = {
  var a = x
  var b = y
  while (a != 0) {
    val temp = a
    a = b % a
    b = temp
  }
  b
}

// A same funcion written recursively.
def gcdLoopR(x: Long, y: Long): Long =
  if (y == 0) x else gcdLoopR(y, x % y)

/* for {{{1 */

val filesHere = new java.io.File(".").listFiles

// Iterate by generator.
for (file <- filesHere)
  println(file)

// Iterate range object.
for (i <- 1 to 4)
  println("Iteration by 'to' " + i)
// This excludes the last digit.
for (i <- 1 until 4)
  println("Iteration by 'until' " + i)

// Iterate with filtering.
for (file <- filesHere if file.getName.endsWith(".md"))
  println(file)


def fileLines(file: java.io.File) =
  scala.io.Source.fromFile(file).getLines().toList

// Multiple generators and filters.
def grep(pattern: String) =
  for (
    file <- filesHere
    if file.getName.endsWith(".md");
    line <- fileLines(file)
    if line.trim.matches(pattern)
  ) println (file + ": " + line.trim)

// Using braces instead of parentheses,
// We can omit semicolons and bind internal results to variables.
def grep2(pattern: String) =
  for {
    file <- filesHere
    if file.getName.endsWith(".md")
    line <- fileLines(file)
    trimmed = line.trim
    if trimmed.matches(pattern)
  } println (file + ": " + trimmed)

grep(".*Scala")
grep2(".*Scala")

// The 'yield' collects each result in the iteration
// and return them as an array.
val forLineLengths =
  for {
    file <- filesHere
    if file.getName.endsWith(".md")
    line <- fileLines(file)
  } yield line.trim.length

println( forLineLengths.reduceLeft( (l1, l2) => l1 + l2 ) )

/* try-catch {{{1 */

// 'throw' can be written in the places which must return some value.
// Of cousrse, 'throw' throws an exception so returns nothing.
val n = 10
val half =
  if (n % 2 == 0)
    n / 2
  else
    throw new RuntimeException(" n must be even")


import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException

val file = new FileReader("readme.md")
try {
  // Manipulate the file.
} catch {
  // Catch exceptions by pattern matching.
  case ex: FileNotFoundException => ex.printStackTrace()
  case ex: IOException => throw new RuntimeException(ex)
} finally {
  file.close()
}

import java.net.URL
import java.net.MalformedURLException

// The 'try-catch' can return values.
// This example will return the default value (url of scala web site)
// if the MalformedURLException is thrown.
def urlFor(path: String) =
  try {
    new URL(path)
  } catch {
    case e: MalformedURLException =>
      new URL("http://www.scala-lang.org")
  }

println(urlFor("invalid:url"))

/* match {{{1 */

val firstArg = if (! args.isEmpty) args(0) else ""

// The pattern matching needs no 'break' and can return value.
val friend =
  firstArg match {
    case "salt"  => "pepper"
    case "chips" => "salsa"
    case "eggs"  => "bacon"
    case _       => "huh?"
  }
println(friend)

/* variable scope {{{1 */

// An example.
def printMultiTable() {
  var i = 1
  while (i <= 10) {
    var j = 1
    while (j <= 10) {
      val prod = (i * j).toString
      var k = prod.length
      while (k < 4) {
        print(" ")
        k += 1
      }
      print(prod)
      j += 1
    }
    println()
    i += 1
  }
}
printMultiTable()

// Refactored version
def makeRowSeq(row: Int) =
  for (col <- 1 to 10) yield {
    val prod = (row * col).toString
    val padding = " " * (4 - prod.length)
    padding + prod
  }
def multiTable() = {
  val tableSeq =
    for (row <- 1 to 10)
      yield makeRowSeq(row).mkString
  tableSeq.mkString("\n")
}
println(multiTable)
