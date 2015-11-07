/* Working with XML */

// In Scala, XML is a first-class object!
val pod = <pod>Three <peas /> in the </pod>
println(pod)

val links =
  <a>
    This is some XML.
    Here is a tag : <atag />
  </a>
println(links)

// Evaluate Scala codes in XML by braces.
val codes =
  <a> { "Hello" + ", world!" }</a><a>{ 3 + 4 }</a>
  <a>{ "<b>This will be escaped</b>" }</a>
println(codes)

// Nested Scala codes and XML.
def yearMade(year: Int) =
  <a>{
    if (year < 2000) <old>{ year }</old>
    else xml.NodeSeq.Empty
  }</a>

println( yearMade(1955) )
println( yearMade(2013) )


abstract class CCTherm {
  val description: String
  val yearMade: Int
  val dateObtained: String
  val bookPrice: Int
  val purchasePrice: Int
  val condition: Int

  override def toString = description

  def toXML =
    <cctherm>
      <description>{description}</description>
      <yearMade>{yearMade}</yearMade>
      <dateObtained>{dateObtained}</dateObtained>
      <bookPrice>{bookPrice}</bookPrice>
      <purchasePrice>{purchasePrice}</purchasePrice>
      <condition>{condition}</condition>
    </cctherm>
}

object CCTherm {
  def fromXML(node: xml.Node): CCTherm =
    new CCTherm {
      val description   = (node \ "description").text
      val yearMade      = (node \ "yearMade").text.toInt
      val dateObtained  = (node \ "dateObtained").text
      val bookPrice     = (node \ "bookPrice").text.toInt
      val purchasePrice = (node \ "purchasePrice").text.toInt
      val condition     = (node \ "condition").text.toInt
    }
}

val therm = new CCTherm {
  val description   = "hot dog #5"
  val yearMade      = 1952
  val dateObtained  = "March 14, 2006"
  val bookPrice     = 2199
  val purchasePrice = 500
  val condition     = 9
}

// Save XML to a file and load it.
xml.XML.save("therm1.xml", therm.toXML)
val thermXml = xml.XML.loadFile("therm1.xml")

println(thermXml)
println( CCTherm.fromXML(thermXml) )


// Use XML with pattern matching.
def proc(node: xml.Node): String =
  node match {
    // Match with all children nodes.
    case <a>{contents @ _*}</a> => "It's an a: " + contents
    case <b>{contents}</b> => "It's a b: " + contents
    case _ => "It's something else."
  }

println( proc(<a>a <em>red</em> apple</a>) )
println( proc(<b>it is b</b>) )
println( proc(<b>it is b. <em>yes!</em></b>) )

// Loop XML nodes.
for (therm @ <cctherm>{_*}</cctherm> <- thermXml)
  println("processing: " + (therm \ "description").text)
