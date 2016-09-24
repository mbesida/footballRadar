package parser

import parser.Parser._
import scala.util.Try
import model.MatchStatistic

object MatchStatparser {
  def homeTeam = get("HomeTeam")(Option.apply)
  def awayTeam = get("AwayTeam")(Option.apply)
  def homeTeamGoals = get("FTHG")(s => numberExtractor(s)(_.toInt))
  def awayTeamGoals = get("FTAG")(s => numberExtractor(s)(_.toInt))
  def homeWinOdds = get("B365H")(s => numberExtractor(s)(_.toDouble))
  def awayWinOdds = get("B365A")(s => numberExtractor(s)(_.toDouble))

  def matchStatistic = (homeTeam & awayTeam & homeTeamGoals & awayTeamGoals & homeWinOdds & awayWinOdds).map {
    case (ht & at & htg & atg & hwo & awo) => MatchStatistic(ht, at, htg, atg, hwo, awo)
  }


  private def numberExtractor[T](str: String)(converter: String => T): Option[T] = Try(converter(str)).toOption
}


