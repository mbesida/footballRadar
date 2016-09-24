package calculation

import org.scalatest.FunSuite
import parser._


class ServiceSuit extends FunSuite{
  import ServiceSuit._
  import Service._

  test("Most profitable teams") {
    val mostProfitables = (read
      andThen parseStatistics
      andThen calculateOddsForTeams
      andThen mostProfitableTeams
      )(inputStream)
    mostProfitables foreach println
  }
}

object ServiceSuit {
  val inputStream = getClass.getClassLoader.getResourceAsStream("09-10.csv")
}
