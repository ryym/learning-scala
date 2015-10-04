# 抽象メンバー

Javaにおいては、抽象クラスにおいて実装をサブクラスに任せる事ができるのはメソッドのみだが、
Scalaでは型、メソッド、val変数、var変数の全てを抽象メンバーとして宣言する事ができる。

```scala

trait Abstract {
  type T
  def transform(x: T): T
  val initial: T
  var current: T
}

class Concrete extends Abstract {
  type T = String
  def transform(x: String) = x + x
  val initial = "hi"
  val current = initial
}

```

## 抽象val

抽象クラスにおいて、以下のように値を与えずに変数のみを宣言すると、それは抽象valとなる。
なお、スーパークラスの`def`をサブクラスが`val`としてオーバーライドする事は可能だが、
その逆はできない。

```scala
abstract class Fruit {
  val name: String
}
```

### 抽象valの初期化

抽象valによって、スーパークラスで使用される値をサブクラスが定義できるようになるため、抽象valをパラメータ
のように使用できる事がある。

```scala

trait RationalTrait {
  val numerArg: Int
  val denomArg: Int
}

// Anonymous class
val half = new RationalTrait {
  val numerArg = 1
  val denomArg = 2
}

```

ただし、抽象valはクラスパラメータと違い、スーパークラスが初期化された**後**に評価されるため、
上記のような方法では、抽象valの値をクラスの初期化時に使用する事はできなくなる。
この問題の対処方法は2つ用意されている。

#### 事前初期化済みフィールド

以下のように抽象valの定義を先に記述すると、スーパークラスの呼び出しより前にサブクラスの抽象valが
評価されるようになる。

```scala

val x = 2
val half = new {
  val numerArg = 1 * x
  val denomArg = 2 * x
} with RationalTrait

class RationalClass(n: Int, d: Int) extends {
  val numerArg = n
  val denomArg = d
} with RationalTrait

val half2 = new RationalClass(1, 2)

```

#### 遅延評価val

通常のval定義によるフィールドは、そのクラスが初期化されるタイミングで一緒に初期化される。
しかし、val宣言の前に`lazy`キーワードを付与すると、そのフィールドは、値が実際に参照される
タイミングで初めて初期化されるようになる。これは`def`における名前渡しパラメータと似ているが、
遅延評価valが評価されるのは最初の1回だけであり、以降は初期化された結果値が再利用されるようになる。

```scala
lazy val numer = numerArg / g
```

## 抽象型

スーパークラスで使用する型の詳細をサブクラスに決定させたい場合は抽象型を使う。
`type`キーワードは単にある型に別名を与えるために使う事もできるが、抽象クラスにおいては
型を抽象するために使用される。

```scala

abstract class Animal {
  type SuitableFood <: Food
  def eat(food: SuitableFood)
}

class Cow extends Animal {
  type SuitableFood = Grass
  override def eat(food: Grass)
}

val bessy = new Cow
bessy eat (new bessy.SuitableFood) // equivalent to 'new Grass'

val tank = new Cow
tank eat (new bessy.SuitableFood)

```

### パス依存型

理解度イマイチ。内部クラスのようで異なるもの。上記の例で言うと`bessy.SuitableFood`。
これは`Cow.SuitableFood`のようには記述できない。
パス依存型は「外部クラスではなく外部オブジェクトに名前を与える」。
`Cow`の`SuitableFood`は`Grass`の別名として定義されているため、
異なる`Cow`インスタンス同士の`SuitableFood`は同じ型となる。

対して、以下のような内部クラスの場合、各インスタンスから参照される
`Inner`は異なるパス依存型となる。

```scala

class Outer {
  class Inner
}

val o1 = new Outer
val o2 = new Outer

var i1: o1.Inner = new o1.Inner
// i1 = new o2.Inner  // Can't be compiled.
var i3: Outer#Inner = null
i3 = new o1.Inner
i3 = new o2.Inner

```

`o1.Inner`と`o2.Inner`は異なるクラスだが(Javaでいう`static`なしの内部クラスのようなもの？)、
両者とも`Outer#Inner`のサブクラスとなる。


### 構造的サブ型

名前を持つ通常のサブクラス(名目的サブ型)以外に、Scalaは構造的なサブ型というものをサポートする。
これは、継承関係を持たない2つの異なる型でも、両者が同じメンバーを持っていれば、それを共通項として
2つの型をサブ型関係として扱う事ができるというものである。

```scala

// `animals`には、`SuitableFood`を`Grass`として定義している`Animal`のサブ型なら
// どんな型でも代入できる。
class Pasture {
  val animals: List[ Animal { type SuitableFood = Grass } ] = Nil
}

// `close()`メソッドさえ持っていれば、どんな型でも`using`に渡す事ができる。
def using[ T <: { def close(): Unit }, S ] (obj: T)(operation: T => S) = {
  val result = operation(obj)
  obj.close()
  result
}

```

## 列挙

Scalaにおける列挙は特別な構文を持たず、`Enumration`クラスの継承とパス依存型の応用で実現される。

```scala

// Valueメソッドは、Enumerationの内部クラスValueのインスタンスを返す。
object Color extends Enumeration {
  val Red, Green, Blue = Value
}

var color: Color.Value = null
color = Color.Red
color = Color.Blue
println( color == Color.Blue ) // => true

```
