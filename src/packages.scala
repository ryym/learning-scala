// Packages
// NOTE:
//  The codes using packages don't work under the REPL environment.
//  To make sure these codes work well, we should just compile this file
//  by the 'scalac' command.

// In scala, we can define multiple packages like this.
package bobsrockets {
  package navigation {
    class Navigator {
      // bobsrockets.navigation.StarMap
      val map = new StarMap
    }

    class StarMap
  }

  // We can omit package names implicitly only when
  // the code is defined directly in the package we want to omit.
  package fleets {
    class Fleet {
      // bobsrockets.Ship
      def addShip() { new Ship }
    }
  }

  class Ship {
    // bobsrockets.navigation.Navigator
    val nav = new navigation.Navigator
  }
}

package launch {
  class Booster3
}

package bobsrockets {
  package launch {
    class Booster2
  }

  package navigation {
    package launch {
      class Booster1
    }

    // Refer to 'unvisible' classes using their package names.
    class MissionControl {
      val booster1 = new launch.Booster1
      val booster2 = new bobsrockets.launch.Booster2
      val booster3 = new _root_.launch.Booster3
    }
  }
}

// Package object
// We can define a package object only once for each package.
// Members of a package object can be seen at any place in the package
// and can be imported as 'bobsrockets.doSomething'.
package object bobsrockets {
  def doSomething() {}
}
