# 関数とクロージャ

## 関数リテラル

Scalaでは関数も第1級オブジェクトであり、変数に格納したり、引数として
別の関数に渡したりできる。変数に格納された関数リテラルは、通常の
関数と同じように括弧をつければ呼び出す事ができる。これは、関数リテラルが
Scalaコンパイラによって`FunctionN`トレイトを拡張する何らかのクラスに
変換されるためである(`Function0`, `Function1`, `Function2`, .., `Function22`)。
各トレイトの数字は引数の数であり、これらは関数を呼び出すための`apply`メソッドを
持っている。Scalaでは`apply`の記述を省略できるので、通常のメソッドと
同じように呼び出す事ができる。

```scala
val increase = (x: Int) => x + 1
println( increase(10) )
println( increase.apply(10) )
```

### プレースホルダの利用

単純な関数リテラルは、アンダースコア(`_`)によるプレースホルダを使うと
更に簡潔に記述できる。プレースホルダは、関数内で各引数が1度しか使われ
ない場合にのみ使用できる。なぜなら複数のプレースホルダは同じ引数の再利用
ではなく、順に第1引数、第2引数、第3引数を表すからである。

```scala
// Using a function literal.
println( someNumbers.filter(x => x > 0) )

// Using a placeholder.
println( someNumbers.filter(_ > 0) )
```

### 部分適用

アンダースコアは引数リスト全体の代わりとして用いる事もできる。
アンダースコアをこのように使うと、Scalaコンパイラは**部分適用された関数**
を生成する。例の`foreach`のように、関数呼び出しが必要とされている場所であれば、
アンダースコアを省略する事ができる。それ以外の場所では、関数名だけを記述しても
自動で部分適用される関数を生成する事はない(see p159)。

```scala
// 0個の引数を部分適用した関数を foreach に渡す。
someNumbers.foreach(println _)
someNumbers.foreach(println)

def divide(a: Int, b: Int): Int = a / b

val divideBy10 = divide(_: Int, 10)
println( divideBy10(30) )
```

### クロージャ

Scalaの関数オブジェクトが関数の外にある変数の値を参照する時、クロージャは変数が
保持している値ではなく、変数自体をつかんでいる。つまりクロージャ作成後に
つかんでいる変数が外部で変更されれば、クロージャもその更新後の値を参照するし、
逆にクロージャが外部の変数の値を変更すれば、その変更は外部でも有効となる。
関数ローカルのように呼び出される度に値が変わるような変数をつかむ場合は、
そのクロージャが定義された時点での変数を参照する(このような場合、関数ローカルの
変数もスタックでなくヒープに確保されるらしい)。

```scala
def divideBy(divisor: Int) = (x: Int) => x / divisor

val divideBy10 = divideBy(10)
val divideBy35 = divideBy(35)

println( divideBy10(70), divideBy35(70) ) // => 7, 2
```

## 特殊な関数呼び出し

### 連続パラメータ

いわゆる可変長引数。他の言語と同じように、連続パラメータは関数の最後の引数でのみ
使用できる。配列を可変長引数として渡す際には特殊な構文を使うので注意が必要。

構文
```scala
def echo(args: String*) =
  for (arg <- args) println(arg)

echo()

echo("Hello")

echo("Hello", "world", "!")

// Pass an array
val arr = Array("What's", "up", "doc?")
echo(arr: _*)
```

### 名前付き引数

名前付き引数も使用できる。位置引数(通常の、順序によりマッチングされる引数の渡し方)との
併用も可能であり、その場合は位置引数を先頭にまとめて指定する。

```scala
def speed(distance: Float, time: Float): Float = distance / time

speec(100, 10) // => 10
speed(time = 10, distance = 100) // => 10
```

### 引数のデフォルト値

デフォルト値付き引数も使える。

```scala

def printTime(out: java.io.PrintStream = Console.out) =
  out.println( "time = " + System.currentTimeMillis() )

// Use default output.
printTime()

printTime(Console.err)
```

### 末尾再帰の最適化

以下のような単純な末尾再帰であれば、Scalaの最適化により`while`を使うコードと同じように
コンパイルしてくれる。

```scala
def approximate(guess: Double): Double =
  if ( isGoodEnough(guess) ) guess
  else approximate( improve(guess) )
```

### 名前渡しパラメータ

名前付き引数と混同しないように。これは引数を受け取らない関数をパラメータとして渡す場合に、
`() =>`という定型記述を省略するための記法(たぶん)。
例えば外部のフラグで実行の有無を設定できる`assert`のような関数を作る場合、普通に関数を
渡すなら以下のようになる。

```scala
var assertionEnabled = true

def myAssert(predicate: () => Boolean) =
  if (assertionEnabled && ! predicate())
    throw new AssertionError

// Throw an exception if the flag is on.
myAssert(() => 2 + 3 < 3)
```

これでは、上記のような単純なアサーションを書く時でも冗長になってしまう。
しかし、フラグによる管理をしたいので式の評価はあくまで`myAssert`の中で行う必要がある。
(式の評価結果を`myAssert`に渡すのでは、フラグに関係なく式が実行されてしまうため
意味がない)。このような場合に名前渡しパラメータを使うと、それこそJavaの`assert`の
ように記述できる。

```scala
var assertionEnabled = true

// '()' is omitted.
def myAssert(predicate: => Boolean) =
  if (assertionEnabled && ! predicate)
    throw new AssertionError

// Throw an exception if the flag is on.
myAssert(2 + 3 < 3)

// OK.
myAssert(true)
```

名前渡しパラメータを使うと、この例でいえば`Boolean`を返す式の評価を、関数内で
その引数が実際に使用されるまで遅らせる事ができる。これにより、冗長な無名関数の
記述を排除できる。
ただし関数を渡す場合は、`myAssert`内で仮引数が評価される度に実行される。

## カリー化

Scalaでは、関数定義において受け取るパラメータリストを増やす事でカリー化された関数を
定義する。既に定義済みの関数をカリー化したい場合には、アンダースコアによる部分適用を
使用すればいい。

```scala
// 通常の関数
def plainOldSum(x: Int, y: Int) = x + y

// カリー化された関数
def curriedSum(x: Int)(y: Int) = x + y

println( plainOldSum(1, 2) ) // => 3
println( curriedSum(1)(2) )  // => 3
```

カリー化された関数に一部の引数だけを渡す場合は、やはりプレースホルダ記法を用いる。
この場合は、プレースホルダの前にスペースを入れる必要はない。

```scala
val onePlus = curriedSum(1)_
val twoPlus = curriedSum(2)_

println( onePlus(2) ) // => 3
println( twoPlus(2) ) // => 4
```

## 波括弧を用いた関数呼び出し

scalaでは、引数を1つだけ受け取る関数の呼び出しに、丸括弧でなく波括弧を使用する事ができる。

```scala
println( "Hello, Scala!" )
println{ "Hello, Scala!" }
```

これを上手く使うと、ユーザー定義関数をまるで組み込みの制御構文のように使用する事ができる。
つまり波括弧を使用する場合、渡される引数は普通関数オブジェクトとなる。
複数のパラメータを渡したい場合はカリー化を使えばいい。

```scala
loanSomeResourceFrom( theFile ) { resource =>
  resource.doSomething("command")
}
```
