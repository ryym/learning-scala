// Functions

import scala.io.Source

// Methods - public and private. {{{1
object LongLines {
  def processFile(fileName: String, width: Int) {
    val source = Source.fromFile(fileName)
    for (line <- source.getLines())
      processLine(fileName, width, line)
  }

  private def processLine(fileName: String, width: Int, line: String) {
    if (line.length > width)
      println(fileName + ": " + line.trim)
  }
}
LongLines.processFile("src/functions.scala", 50)

// Local functions. {{{1
def processFile(fileName: String, width: Int) {
  def processLine(line: String) {
    if (line.length > width)
      println(fileName + ": " + line.trim)
  }

  val source = Source.fromFile(fileName)
  for (line <- source.getLines())
    processLine(line)
}
processFile("src/functions.scala", 50)

// First-class functions. {{{1

// increase: (Int) => Int = <function1>
var increase = (x: Int) => x + 1
println(increase(10))
println(increase.apply(10))

increase = (x: Int) => x + 9999
println(increase(10))

// Function-literal with multiple line
increase = (x: Int) => {
  println("We")
  println("are")
  println("here!")
  x + 1
}
println(increase(10))

// Pass function literals as a parameter.
// Types and parentheses can be omitted.

val someNumbers = List(-11, -10, -5, 0, 5, 10)
someNumbers.foreach( (x: Int) => println(x) )
println( someNumbers.filter( x => x > 0 ) )

// Using placeholders.

println( someNumbers.filter( _ < -1 ) )
println( someNumbers.reduce( _ + _ ) )

// Partially applied functions. {{{2

def sum(a: Int, b: Int, c: Int) = a + b + c
println( sum(1, 2, 3) )

// (Int, Int, Int) => Int = <function3>
// No arguments is applied.
val a = sum _
println( a(1, 2, 3) ) // 6

// (Int) => Int = <function1>
// Only second argument is not applied.
val b = sum(1, _: Int, 3)
println( b(2) ) // 6
