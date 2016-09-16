import swing._
import event._

object TempConverter extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "Celsius / Fahrenheit Converter"

    // reactions 内のパターンマッチで使うためにオブジェクトを定義する。
    object celsius extends TextField {
      columns = 5
    }
    object fahrenheit extends TextField {
      columns = 5
    }

    contents = new FlowPanel {
      contents += celsius
      contents += new Label(" Celsius = ")
      contents += fahrenheit
      contents += new Label(" Fahrenheit")
      border = Swing.EmptyBorder(15, 10, 10, 10)
    }

    listenTo(celsius, fahrenheit)

    // 変数名と解釈されないよう、バッククォートで囲う。
    // EditDone(foo) だと全ての EditDone イベントにマッチしてしまう。
    // celsius などのオブジェクト名を大文字始まりにする事でも回避できる。
    // (p.267 15.2節)
    reactions += {
      case EditDone(`fahrenheit`) =>
        val f = fahrenheit.text.toInt
        val c = (f - 32) * 5 / 9
        celsius.text = c.toString
      case EditDone(`celsius`) =>
        val c = celsius.text.toInt
        val f = c * 9 / 5 + 32
        fahrenheit.text = f.toString
    }
  }
}
