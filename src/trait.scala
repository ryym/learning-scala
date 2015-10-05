// Trait

// A simple example.
trait Philosophical {
  def philosophize() {
    println("I consume memory,  therefore I am!")
  }
}

// Use a trait.
class Student extends Philosophical
new Student().philosophize()

// Use a trait with a super class.
class Animal
class Frog extends Animal with Philosophical
new Frog().philosophize()

/* An example which extends Rational class with Ordered trait is in 'Rational.scala'. */

// Stackable modifications

import scala.collection.mutable.ArrayBuffer

abstract class IntQueue {
  def get(): Int
  def put(x: Int)
}

// Basic implementation.
class BasicIntQueue extends IntQueue {
  private val buf = new ArrayBuffer[Int]
  def get() = buf.remove(0)
  def put(x: Int) { buf += x }
}

val biq = new BasicIntQueue
biq.put(10); biq.put(20)
println( biq.get(), biq.get() )

// The trait which doubles the specified value.
trait Doubling extends IntQueue {
  abstract override def put(x: Int) { super.put(2 * x) }
}

// The trait which increments the specified value.
trait Incrementing extends IntQueue {
  abstract override def put(x: Int) { super.put(x + 1) }
}

// The trait which put the specified value only if the value is a positive number.
trait Filtering extends IntQueue {
  abstract override def put(x: Int) {
    if (x >= 0) super.put(x)
  }
}

// Define a class with a super class and a trait.
class DoublingBasicIntQueue extends BasicIntQueue with Doubling

val dbiq = new DoublingBasicIntQueue
dbiq.put(10); dbiq.put(20)
println( dbiq.get(), dbiq.get() )

// Trait can be mixed-in at instantiating.
val queue = new BasicIntQueue with Incrementing with Filtering
queue.put(-1); queue.put(0); queue.put(1)
println( queue.get(), queue.get() )

// The functionality of the instance changes depending on the order of traits.
val queue2 = new BasicIntQueue with Filtering with Incrementing
queue2.put(-1); queue2.put(0); queue2.put(1)
println( queue2.get(), queue2.get() )
