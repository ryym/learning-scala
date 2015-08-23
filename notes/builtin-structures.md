# 組み込みの制御構造について

Scalaの組込み制御構造は、関数呼び出しを除くと以下のもののみ。
```
if, while, for, try, match
```
そしてこれらの内、`while`以外は文ではなく式である。つまり何らかの
値を返す事ができる(`while`は`Unit`型を返す)。
各制御文については、[ソースファイル](../src/builtinStructures.scala)を参照。

## break, continue

Scalaはループ処理を途中で抜け出すための`break`や`continue`を組み込み構文としては
用意していない。これは、これらの構文が関数型リテラルとは相性が悪いため。
ただし、メソッドと例外処理によって`break`処理を実現する`breakable`という
ブロック構文が標準ライブラリには用意されている(see p143)。

## 変数のスコープ

Javaとほぼ同じだが、Scalaでは外側のスコープと同じ名前を持つ変数を
内側のスコープで作る事ができる。

```scala

val a = 1;
{
  val a = 2
  println(a) // => 2
}
println(a) // => 1

```
