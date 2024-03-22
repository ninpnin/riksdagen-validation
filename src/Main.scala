package main
import scalatags.Text.all._
import mainargs.{main, ParserForMethods}
/*
object Main {

  def main(args: Array[String]): Unit = println("args ", args(0))
}
*/
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.{Schema, SchemaFactory, Validator}
import org.xml.sax.{ErrorHandler, SAXParseException}
import java.net.URL 


object Main {

  def main(args: Array[String]): Unit = {
    println("Lets validate lets goo")
    println("Args " + args(0) + " " + args(1))
    val results = validate(args(0), args(1))

    println(results)
  }

  def validate(xmlFile:String,xsdFile:String): Unit ={
    println("We are in the validation function now")
    var exceptions = List[String]()
    try {
      println("pt1")
      val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
      //val url = this.getClass().getResource(xsdFile)//ClassLoader.getSystemResource(xsdFile)
      val url = new URL("file://" + System.getProperty("user.dir") + "/" + xsdFile)
      println(xsdFile, url)
      val streamSource = new StreamSource(url.openStream())
      println("pt1.5")
      val schema: Schema = schemaFactory.newSchema(streamSource)
      println("pt2")

      val validator: Validator = schema.newValidator()
      validator.setErrorHandler(new ErrorHandler() {
        @Override
        def warning(exception:SAXParseException){

          exceptions = exception.getMessage  :: exceptions
        }
        @Override
        def fatalError(exception:SAXParseException ) {
          exceptions = exception.getMessage  :: exceptions
        }
        @Override
        def error(exception:SAXParseException ) {
          exceptions = exception.getMessage  :: exceptions
        }
      });

      val xmlUrl = new URL("file://" + System.getProperty("user.dir") + "/" + xmlFile) // ClassLoader.getSystemResource(xmlFile)
      validator.validate(new StreamSource(xmlUrl.openStream()))
      exceptions.foreach(e=>{
        println(e)
      })
    } catch {
      case ex => {
        println("Exception??")
        ex.getMessage
      }
    }
  }

}