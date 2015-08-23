# 型について

## Unit

### voidとの違い
`Unit`型は、副作用のある手続き関数（値を返さない関数）が返す値であり、Javaの`void`に相当する。
ただしJavaの`void`と異なり、`Unit`型には`()`という値がただ1つ存在する。

```scala
def greet() { println("Hi") }
greet() == () // true
```

### 代入式の戻り値
例えば以下のようなJavaコードはScalaでは上手く動かない。
これは、Scalaにおいては代入式の結果値は代入された値ではなく、
常に`Unit`型になるため。(see p.131)  
ちなみに、`while`文も`Unit`を返す。

```java
while ((line = br.readLine()) != "")
    System.out.println(line);
```

## コレクション

### List
ScalaのListは`const`演算子を持っている。また末尾に追加する`append`メソッドも
あるものの、こちらは追加にかかる時間がリストサイズに比例してしまうらしい。

```scala
val nums1 = List(1, 2, 3)
val nums2 = 4 :: 5 :: 6 :: Nil
val nums3 = nums1 ::: nums2
println( nums3(2) )
nums3.map( n => n * 2 )
```

### Set, Map

Scalaの標準ライブラリにはミュータブルな集合とイミュータブルな集合の両方がある。
SetもMapも、演算子メソッドによってシンプルな記述で操作する事ができる。

```scala
// Immutable set
var jetSet = Set("Boeing", "Airbus")
jetSet += "Lear" // This creates the new instance.
println(jetSet.contains("Cessna"))
```

```scala
// Immutable map
import scala.collection.mutable.Map
val treasureMap = Map[Int, String]()
treasureMap += (1 -> "Go to island.")
treasureMap += (2 -> "Find big X on ground.")
treasureMap += (3 -> "Dig.")
println(treasureMap(2))

val japaneseNumeral = scala.collection.immutable.Map(
  1 -> "一", 2 -> "二",
  3 -> "三", 4 -> "四",
  5 -> "五"
)
println(japaneseNumeral(5))
```

特にMapの`key -> value`という記法は以下にも組み込みの構文のようだが、
`->`はScalaの全てのオブジェクトが呼び出せるメソッドであり(暗黙の型変換)、
キーと値の2要素を格納するタプルを返す。

```scala
val pair = "key" -> "value"
```
