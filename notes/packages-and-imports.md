# パッケージとインポート

## パッケージ
[ソースファイル](../src/packages.scala)参照。

## 暗黙のインポート
全てのscalaファイルは暗黙の内に以下のパッケージをインポートしている。

```scala
import java.lang._  // String, Thread, ..
import scala._      // List, Array, ..
import Predef._     // assert, ..
```

## アクセス修飾子
Scalaで明示的に指定できるアクセス修飾子は`private`と`protected`のみであり、
省略した場合は全て`public`(どこからでも参照可能)となる。ただし、それぞれ
Javaとは以下のような微妙な違いがある。

* private
    * あるクラスAの内部クラスBで定義された`private`なメソッドやフィールドは、
    クラスAからはアクセスできない。クラスBの内部クラスCからはアクセス可能。
* protected
    * protectedなメンバーにアクセスできるのはサブクラスからのみであり、Java
    のように同パッケージの他クラスからも見えるという事はない。

### スコープの細かな制御
修飾子は`private`と`protected`だけでも、`private[X]`のような限定子を用いた修飾子
によって非常にきめ細やかなアクセス制御を実現できる。
例:
```scala
package sample

class Outer {
  // Javaの`protected`と同じになる。
  protected[sample] val value: Int

  class Inner {
    // Javaの`private`と同じになる。
    private[Outer] def doSomething() {}
  }
}
```
