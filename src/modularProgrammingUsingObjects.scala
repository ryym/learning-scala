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

object FruitSalad extends Recipe(
  "fruit salad",
  List(Apple, Orange, Cream, Sugar),
  "Stir it all together."
)

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
abstract class Database {
  case class FoodCategory(name: String, foods: List[Food])

  def allFoods: List[Food]
  def allRecipes: List[Recipe]
  def allCategories: List[FoodCategory]

  def foodNamed(name: String): Option[Food] =
    allFoods.find(_.name == name)
}

// Multiple subclasses

object SimpleBrowser extends Browser {
  val database = SimpleDatabase
}

object SimpleDatabase extends Database {
  def allFoods = List(Apple, Orange, Cream, Sugar)
  def allRecipes = List(FruitSalad)
  def allCategories = categories

  private var categories = List(
    FoodCategory("fruits", List(Apple, Orange)),
    FoodCategory("misc", List(Cream, Sugar))
  )
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

  def allFoods = List(FrozenFood)
  def allRecipes = List(HeatItUp)
  def allCategories = List(
    FoodCategory("edible", List(FrozenFood))
  )
}

println(SimpleBrowser.recipesUsing(Apple))
println(StudentBrowser.recipesUsing(Apple))
