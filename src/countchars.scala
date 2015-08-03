// Variables and functions

import scala.io.Source

def widthOfLength(s: String) = s.length.toString.length

def mapLinesWithCount(lines: List[String]): List[String] = {
  val longestLine = lines.reduceLeft(
    (a, b) => if (a.length > b.length) a else b
  )
  val maxLength = widthOfLength(longestLine)
  lines.map( line => {
    val numSpaces = maxLength - widthOfLength(line)
    val padding   = " " * numSpaces
    padding + line.length + " | " + line
  })
}

if (0 < args.length) {
  val lines = Source.fromFile( args(0) ).getLines().toList
  mapLinesWithCount(lines).foreach(println)
}
else
  Console.err.println("Please enter filename")
