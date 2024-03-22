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

object Main {

  def main(args: Array[String]): Unit = {
    println("Lets validate lets goo")
    val results = validate("test-broken.xml","test.xsd")
    println(results)
  }

  def validate(xmlFile:String,xsdFile:String): Unit ={
    println("We are in the validation function now")
    var exceptions = List[String]()
    try {
      println("pt1")
      val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
      val url = ClassLoader.getSystemResource(xsdFile)
      println("pt1.4")
      println(url)
      val schema: Schema = schemaFactory.newSchema(new StreamSource(url.openStream()))
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

      val xmlUrl = ClassLoader.getSystemResource(xmlFile)
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