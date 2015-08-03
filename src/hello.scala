// My first code of Scala

println("Hello world, from a script!")

if (0 < args.length) {
  println("Hello " + args(0) + "!")

  args.foreach((arg: String) => println(arg))

  args.foreach(println)

  for (arg <- args) println(arg)
}

// Operator is method!
println(1 + 2, 1.+(2))

def applyAndUpdate() = {
  val arr1 = Array(1,2,3)
  val arr2 = Array.apply(1,2,3)
  println(arr1(0), arr1.apply(0))
  arr2(0) = -1
  arr2.update(0, -1)
}
applyAndUpdate()

