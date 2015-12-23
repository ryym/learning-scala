/* Modular programming using objects */

abstract class Food(val name: String) {
  override def toString = name
}

class Recipe(
  val name: String,
  val ingredients: List[Food],
  val instructions: String
) {
  override def toString = name
}

object Apple  extends Food("Apple")
object Orange extends Food("Orange")
object Cream  extends Food("Cream")
object Sugar  extends Food("Sugar")

// Abstract class to browse foods and recipes.
abstract class Browser {
  val database: Database

  def recipesUsing(food: Food) =
    database.allRecipes.filter(recipe =>
      recipe.ingredients.contains(food)
    )

  def displayCategory(category: database.FoodCategory) =
    println(category)
}

// Abstract class which fetches recipe data.
abstract class Database extends FoodCategories {
  def allFoods: List[Food]
  def allRecipes: List[Recipe]

  def foodNamed(name: String): Option[Food] =
    allFoods.find(_.name == name)
}

trait FoodCategories {
  case class FoodCategory(name: String, foods: List[Food])
  def allCategories: List[FoodCategory]
}

// Multiple databases and browsers

object SimpleBrowser extends Browser {
  val database = SimpleDatabase
}

object SimpleDatabase
  extends Database
  with SimpleFoods
  with SimpleRecipes

trait SimpleFoods {
  object Pear extends Food("Pear")
  def allFoods = List(Apple, Pear)
  def allCategories = Nil
}

trait SimpleRecipes {
  // Using self-type, SimpleRecipes can access
  // the members of SimpleFoods. Therefore this requires
  // a class which extends SimpleRecipes also extends SimpleFoods.
  this: SimpleFoods =>

  object FruitSalad extends Recipe(
    "fruit salad",
    List(Apple, Pear),
    "Mix it all together."
  )
  def allRecipes = List(FruitSalad)
}

object StudentBrowser extends Browser {
  val database = StudentDatabase
}

object StudentDatabase extends Database {
  object FrozenFood extends Food("FrozenFood")

  object HeatItUp extends Recipe(
    "heat it up",
    List(FrozenFood),
    "Microwave the 'food' for 10 minutes."
  )

  object ApplePie extends Recipe(
    "apple pie",
    List(Apple, Sugar),
    "I don't know!"
  )

  def allFoods = List(FrozenFood)
  def allRecipes = List(HeatItUp, ApplePie)
  def allCategories = List(
    FoodCategory("edible", List(FrozenFood))
  )
}

println(SimpleBrowser.recipesUsing(Apple))
println(StudentBrowser.recipesUsing(Apple))

object GotApples {
  def showRecipesFrom(database: String) {
    val db: Database =
      if (database == "simple")
        SimpleDatabase
      else
        StudentDatabase

    // Define a browser with a database assigned dynamically.
    object browser extends Browser {
      val database = db
    }

    val apple = SimpleDatabase.foodNamed("Apple").get
    for (recipe <- browser.recipesUsing(apple))
      println(recipe)
  }
}

GotApples.showRecipesFrom("simple")
GotApples.showRecipesFrom("student")
