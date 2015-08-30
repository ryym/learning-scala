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
