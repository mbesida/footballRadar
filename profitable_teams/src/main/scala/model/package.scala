/**
  * Created by mbesida on 25.09.2016.
  */
package object model {

  /**
    * If profit is negative then it is a loss
    */
  case class BetResult(team: String, profit: Double)

  case class MatchStatistic(homeTeam: String,
                            awayTeam: String,
                            fthg: Int,
                            ftag: Int,
                            homeWinOdds: Double,
                            awayWinOdds:Double)

}
