import core.RiotApi
import core.config.Regions.{PlatformRegion, RegionalRoute}
import core.config.RiotApiConfig
import core.http.JavaNetRiotHttp
import io.github.cdimascio.dotenv.Dotenv
import wrapper.mapping.MatchMapper

import java.time.Duration

@main
def main(): Unit = {
  val dotenv = Dotenv.load()
  val apiKey = dotenv.get("RIOT_API_KEY")

  val config = new RiotApiConfig(apiKey, PlatformRegion.EUW1, RegionalRoute.EUROPE, Duration.ofSeconds(10))

  val riotHttp = new JavaNetRiotHttp(config)
  val riotApi = new RiotApi(riotHttp)

  /*
  val account = riotApi.account().byRiotId(RegionalRoute.EUROPE, "Thayger", "Soul")
  println(account)
  */

  val server = new SimpleHttpServer(8080)

  server.get("/matchhistory") { exchange =>
      try
        val params = server.queryParams(exchange)

        val name = params.getOrElse("name", "")
        val tag = params.getOrElse("tag", "")
        val region = params.getOrElse("region", "EUROPE")
        val limit = params.get("limit").map(_.toInt).getOrElse(5)

        val regional = RegionalRoute.valueOf(region)

        val account = riotApi.account().byRiotId(regional, "thayger", "soul")

        val puuid = account.puuid()

        val matchIds = riotApi.`match`().getListOfMatchIdsByPuuid(regional, puuid, 0, limit)

        println(account)
        println(matchIds)

      catch
        case e: Exception =>
          e.printStackTrace()
          server.sendResponse(exchange, 500, s"""{"error":"${e.getMessage}"}""")
  }

  server.start()
}