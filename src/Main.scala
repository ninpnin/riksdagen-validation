package main
import scalatags.Text.all._
import scala.math.min
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
    val results = validate(args.drop(1), args.head)
    println(results)
  }


  def validate(xmlFiles: Array[String], xsdFile: String): Unit ={
    var exceptions = List[String]()
    try {
      val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
      //val url = this.getClass().getResource(xsdFile)//ClassLoader.getSystemResource(xsdFile)
      val url = new URL("file://" + System.getProperty("user.dir") + "/" + xsdFile)
      println("Schema: " + url)
      val streamSource = new StreamSource(url.openStream())
      val schema: Schema = schemaFactory.newSchema(streamSource)

      val validator = schema.newValidator()
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

      def validateFile(xmlFile: String) = {
        println("Validate: " + xmlFile)
        exceptions = List[String]()
        val xmlUrl = new URL("file://" + System.getProperty("user.dir") + "/" + xmlFile) // ClassLoader.getSystemResource(xmlFile)
        validator.validate(new StreamSource(xmlUrl.openStream()))
        exceptions.foreach(println(_))
        println("Number of exceptions: " + exceptions.length)
        (xmlFile, exceptions.length)
      }
      val results = for (xmlFile <- xmlFiles) yield validateFile(xmlFile)
      val resultMap = results.toMap
      val filesWithExceptions = resultMap.filter(_._2 >= 1).toVector
      if (filesWithExceptions.length == 0) {
        println("All files adhere to the schema.")
      } else {
        println("Out of the " + xmlFiles.length + " files " + filesWithExceptions.length + " do not adhere to the schema")
        val exceptionDescriptions = filesWithExceptions.map(x =>  x._1 + ": " + x._2 + " errors")
        val exceptionDescription = exceptionDescriptions.reduceLeft(_ + "\n" + _)
        println(exceptionDescription)
      }

    } catch {
      case ex => {
        println("Exception in the validation.")
        println("Exception message: " + ex.getMessage)
      }
    }
  }

}