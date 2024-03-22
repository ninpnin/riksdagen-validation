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
    val results = validate(args.drop(1), args.head)
    println(results)
  }

  def validate(xmlFiles: Array[String], xsdFile: String): Unit ={
    var exceptions = List[String]()
    try {
      val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
      //val url = this.getClass().getResource(xsdFile)//ClassLoader.getSystemResource(xsdFile)
      val url = new URL("file://" + System.getProperty("user.dir") + "/" + xsdFile)
      println("Schema:" + url)
      val streamSource = new StreamSource(url.openStream())
      val schema: Schema = schemaFactory.newSchema(streamSource)

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

      for (xmlFile <- xmlFiles) {
        println("Validate:" + xmlFile)
        exceptions = List[String]()
        val xmlUrl = new URL("file://" + System.getProperty("user.dir") + "/" + xmlFile) // ClassLoader.getSystemResource(xmlFile)
        validator.validate(new StreamSource(xmlUrl.openStream()))
        exceptions.foreach(e=>{
          println(e)
        })
        println("Number of exceptions " + exceptions.length)
      }
    } catch {
      case ex => {
        println("Exception in the validation??")
        println("Exception message" + ex.getMessage)
      }
    }
  }

}