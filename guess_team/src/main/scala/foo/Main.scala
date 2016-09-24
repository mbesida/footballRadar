package foo

import dispatch._
import scala.concurrent.ExecutionContext.Implicits.global
import javax.script.ScriptEngineManager
import scala.xml.XML
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.collection.JavaConversions._
import java.net.{CookieManager, CookieHandler}


/**
  *  1. Goes to https://www.footballradar.com/quiz/ to get template url for favourite team link
  *  2. Parses template and goes to that link
  *
  *  NOTE: The idea is that you have to do it very quickly as favourite team link has expiration period of 1 second,
  *  so it's almost impossible to do it manually.
  */
object Main {
  CookieHandler.setDefault(new CookieManager()) //make sure cookies are turned on

  val SourceUrl = "https://www.footballradar.com/quiz/"
  val targetUrlTemplate = s"${SourceUrl}answer"
  val ExpressionPattern = """.*\{(.*)\}.*""".r
  val JavascriptEngine = new ScriptEngineManager().getEngineByName("JavaScript")

  def evalExpression(str: String): Int = JavascriptEngine.eval(str).toString.toDouble.toInt

  def main(args: Array[String]) {
    val favoutiteTeamResponse = Http(url(SourceUrl)) flatMap { response =>
      (XML.loadString(response.getResponseBody) \\ "h3").text match {
        case ExpressionPattern(exp) => {
          val targetUrl = s"${targetUrlTemplate}/${evalExpression(exp)}"
          val targetSvc = response.getCookies.foldLeft(url(targetUrl)){
            (req, cookie) =>
              req.addCookie(cookie)
          }
          Http(targetSvc OK as.String)
        }
      }
    }
    println(Await.result(favoutiteTeamResponse, 1 second))
    Http.shutdown()
  }



}
