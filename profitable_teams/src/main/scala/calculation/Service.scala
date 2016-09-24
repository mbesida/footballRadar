package calculation

import parser.{MatchStatparser, Row}
import model.MatchStatistic
import parser.Success
import model.BetResult


object Service {

  val Bet = 10 //10 pounds

  val parseStatistics: Stream[Row] => Stream[MatchStatistic] = rows =>{
    val parseResults = rows.map(MatchStatparser.matchStatistic)
    val (success, errors) = parseResults.partition(_.isSuccess)
    if (errors.nonEmpty) throw new RuntimeException("Oops")
    else success.collect{case Success(value) => value}
  }

  val calculateOddsForTeams: Seq[MatchStatistic] => Seq[BetResult] = stats =>{
    val homeStats = stats.groupBy(_.homeTeam)
    val awayStats = stats.groupBy(_.awayTeam)
    val betResults = homeStats.map {case(homeTeam, homeTeamStats) =>
      val awayStatForTeam = awayStats(homeTeam)
      val homeProfit = profitForTeam(homeTeamStats){stat => (stat.fthg > stat.ftag, stat.homeWinOdds)}
      val awayProfit = profitForTeam(awayStatForTeam){stat => (stat.ftag > stat.fthg, stat.awayWinOdds)}
      BetResult(homeTeam, homeProfit + awayProfit)
    }
    betResults.toSeq
  }

  val mostProfitableTeams: Seq[BetResult] => Seq[BetResult] = results =>
    results.sortWith((first, second) => first.profit > second.profit)

  private def profitForTeam(stats: Seq[MatchStatistic])(homeAwayFunc: (MatchStatistic => (Boolean, Double))):Double = {
    stats.foldLeft(0D){(res, currentStat) =>
      if (homeAwayFunc(currentStat)._1) res + Bet * homeAwayFunc(currentStat)._2
      else res - Bet
    }
  }

}
