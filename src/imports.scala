// Imports

package table {
  package bascket {
    abstract class Fruit (
      val name: String,
      val color: String
    )
    object Fruits {
      object Apple extends Fruit("apple", "red")
      object Orange extends Fruit("orange", "orange")
      object Pear extends Fruit("pear", "yellowish")
      val menu = List(Apple, Orange, Pear)
    }
  }

  package vase {
    class Rose
    class Sunflower
    class Lily
  }

  package pencilcase {
    class Pencil
    class Eraser
    class MagicMarker
  }

  package pc {
    object Dynabook
    object Vaio
    object MacbookAir
  }
}

// Import Fruit class.
import table.bascket.Fruit

// Import all flower classes.
import table.vase._

// Import only specific fruits.
import table.bascket.Fruits.{Orange, Pear}

// Import a package.
import java.util.regex
class AStarB {
  val pat = regex.Pattern.compile("a*b")
}

// Import with an alias.
import table.bascket.Fruits.{Apple => McIntosh}
import java.{sql => S}

// Import all classes and rename the specific one.
import table.pencilcase.{MagicMarker => Marker, _}

// Import all PCs except Dynabook.
import table.pc.{Dynabook => _, _}

class Importer {
  // Import an instance.
  // Same as 'println(fruit.name + "s are " + fruit.color)'.
  def showFruit(fruit: Fruit) {
    import fruit._
    println(name + "s are " + color)
  }
}
