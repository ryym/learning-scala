// The Architecture of Scala Collections

import collection._
import collection.mutable.{Builder, ArrayBuffer, MapBuilder}
import collection.generic.CanBuildFrom

/* RNA Base chain */

abstract class Base
case object A extends Base
case object T extends Base
case object G extends Base
case object U extends Base
object Base {
  // `Int => Base` is assignable from `WrappedArray[Base]` and
  // `Base => Int` is assignable from `Map[Base, Int]`
  // (but I don't know how ..).
  val fromInt: Int => Base = Array(A, T, G, U)
  val toInt: Base => Int = Map(A -> 0, T -> 1, G -> 2, U -> 3)
}

/*
 * RNA鎖を表すコレクションクラス
 * 塩基は4種類しかないので、各塩基を2ビットの値に割り当て、
 * 保持する整数のリストの各値に16個の塩基を格納する。
 */
final class RNA private (
  val groups: Array[Int],
  val length: Int
) extends IndexedSeq[Base]
  with IndexedSeqLike[Base, RNA] {
  import RNA._

  // `IndexedSeqLike`の抽象メソッドを実装する。
  // `mapResult`には、`ArrayBuffer`を`RNA`に変換するための関数を渡す。
  // これにより、`IndexedSeqLike`内で実装されている`take`や`drop`などの
  // 各種メソッドを、`RNA`型を保ったまま使用する事ができる。
  override def newBuilder: Builder[Base, RNA] = RNA.newBuilder

  def apply(idx: Int): Base = {
    if (idx < 0 || length <= idx)
      throw new IndexOutOfBoundsException
    Base.fromInt( groups(idx / N) >> (idx % N * S) & M )
  }

  // `RNA`クラスに最適化された`foreach`を再実装する。
  override def foreach[U](f: Base => U): Unit = {
    var i, b = 0
    while (i < length) {
      b = if (i % N == 0) groups(i / N) else b >>> S // Fill the new left bits with zeroes.
      f( Base.fromInt(b & M) )
      i += 1
    }
  }
}

object RNA {
  private val S = 2            // グループを表現するために必要なビット数
  private val N = 32 / S       // Intに収まるグループの数
  private val M = (1 << S) - 1 // グループを分離するビットマスク

  def fromSeq(buf: Seq[Base]): RNA = {
    val groups = new Array[Int]( (buf.length + N - 1) / N )
    for (i <- 0 until buf.length)
      groups(i / N) |= Base.toInt( buf(i) ) << (i % N * S)
    new RNA(groups, buf.length)
  }

  def apply(bases: Base*) = fromSeq(bases)

  def newBuilder: Builder[Base, RNA] =
    new ArrayBuffer[Base] mapResult fromSeq

  // `map`や`++`などは`Base`以外の任意の要素型を持つコレクションを
  // 生成しなければならない可能性があるため、`RNA`型を保ったまま
  // これらのメソッドを使用するためには、別途`RNA[Base] -> RNA[Base]`
  // のための`CanBuildFrom`を`implicit`定義する必要がある。
  implicit def canBuildFrom: CanBuildFrom[RNA, Base, RNA] =
    new CanBuildFrom[RNA, Base, RNA] {
      def apply(): Builder[Base, RNA] = newBuilder
      def apply(from: RNA): Builder[Base, RNA] = newBuilder
    }
}

var rna = RNA(A, U, G, G, T)
println( rna ++ List("missing") )
println( rna map Base.toInt )
println( rna ++ RNA(T, A) )
println( rna map { case G => T case b => b } )
println( rna )
println( rna take 3 )
println( rna filter (U != _) )


/* Patricia trie */
// 'Practical Algorithm to Retrieve Information Coded in Alphanumeric'

/*
 * パトリシアルトライを使って文字列と値を紐付けるマップ
 * 与えられる文字列の各文字をツリー構造として保持し、最後の文字のノードに
 * その文字列に対応する値を持たせる。キーとなる各文字列に共通する
 * 文字列部分はノードを共有し、ある文字はそれに続く文字列をキーとする
 * 全てのサブコレクションを持つ形になる。
 */
class PrefixMap[T]
extends mutable.Map[String, T]
    with mutable.MapLike[String, T, PrefixMap[T]] {
  var suffixes: immutable.Map[Char, PrefixMap[T]] = Map.empty
  var value: Option[T] = None

  def get(s: String): Option[T] =
    if (s.isEmpty) value
    else suffixes get ( s(0) ) flatMap ( _.get(s substring 1) )

  def withPrefix(s: String): PrefixMap[T] =
    if (s.isEmpty) this
    else {
      val leading = s(0)
      suffixes get leading match {
        case None =>
          suffixes = suffixes + (leading -> empty)
        case _ =>
      }
      suffixes(leading) withPrefix (s substring 1)
    }

  override def update(s: String, elem: T) =
    withPrefix(s).value = Some(elem)

  override def remove(s: String): Option[T] =
    if (s.isEmpty) {
      val prev = value
      value = None
      prev
    } else {
      suffixes get( s(0) ) flatMap ( _.remove(s substring 1) )
    }

  def iterator: Iterator[(String, T)] = {
    val root  = for (v <- value.iterator) yield ("", v)
    val nodes = for {
      (chr, m) <- suffixes.iterator
      (s, v)   <- m.iterator
    } yield (chr +: s, v)
    root ++ nodes
  }

  def += (kv: (String, T)): this.type = {
    update(kv._1, kv._2)
    this
  }

  def -= (s: String): this.type = {
    remove(s)
    this
  }

  override def empty = new PrefixMap[T]
}

object PrefixMap extends {
  def empty[T] = new PrefixMap[T]

  def apply[T](kvs: (String, T)*): PrefixMap[T] = {
    val m: PrefixMap[T] = empty
    for (kv <- kvs) m += kv
    m
  }

  def newBuilder[T]: Builder[ (String, T), PrefixMap[T] ] =
    new MapBuilder[String, T, PrefixMap[T]](empty)

  // XXX: What is `PrefixMap[_]`..?
  implicit def canBuildFrom[T]
    : CanBuildFrom[ PrefixMap[_], (String, T), PrefixMap[T] ] = {
      new CanBuildFrom[ PrefixMap[_], (String, T), PrefixMap[T] ] {
        def apply(from: PrefixMap[_]) = newBuilder[T]
        def apply() = newBuilder[T]
      }
    }
}

val pm = PrefixMap("hello" -> 5, "hi" -> 2)
println(pm)
for ( (k, v) <- pm.iterator )
  println( " k: " + k + " v: " + v )
println( pm map { case (k, v) => (k + "!", "x" * v) } )
