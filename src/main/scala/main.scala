import core.RiotApi
import core.config.Regions.{PlatformRegion, RegionalRoute}
import core.config.RiotApiConfig
import core.http.JavaNetRiotHttp
import io.github.cdimascio.dotenv.Dotenv
import wrapper.service.RiotLeagueService

import java.time.Duration

@main
def main(): Unit = {
  val dotenv = Dotenv.load()
  val apiKey = dotenv.get("RIOT_API_KEY")

  val config = new RiotApiConfig(apiKey, PlatformRegion.EUW1, RegionalRoute.EUROPE, Duration.ofSeconds(10))

  val riotHttp = new JavaNetRiotHttp(config)
  val riotApi = new RiotApi(riotHttp)
  val service = new RiotLeagueService(riotApi)

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

        if name.isEmpty || tag.isEmpty then
          server.sendResponse(exchange, 400, """{"error":"Missing name or tag"}""")

        val regional = RegionalRoute.valueOf(region)

        val summaries = service.getMatchHistory(regional, name, tag, limit)

        val sb = new StringBuilder
        sb.append("[")

        val it = summaries.iterator()
        var idx = 0
        while it.hasNext do
          val s = it.next()

          if idx > 0 then sb.append(",")

          sb.append("{")
          sb.append("\"matchId\":\"").append(s.matchId()).append("\",")
          sb.append("\"gameMode\":\"").append(s.gameMode()).append("\",")
          sb.append("\"durationSeconds\":").append(s.durationSeconds()).append(",")
          sb.append("\"champion\":\"").append(s.champion()).append("\",")
          sb.append("\"kills\":").append(s.kills()).append(",")
          sb.append("\"deaths\":").append(s.deaths()).append(",")
          sb.append("\"assists\":").append(s.assists()).append(",")
          sb.append("\"win\":").append(s.win())
          sb.append("}")

          idx += 1

        sb.append("]")

        server.sendResponse(exchange, 200, sb.toString)
      catch
        case e: Exception =>
          e.printStackTrace()
          server.sendResponse(exchange, 500, s"""{"error":"${e.getMessage}"}""")
  }

  server.get("/summoner") { exchange =>
    try
      val params = server.queryParams(exchange)

      val name = params.getOrElse("name", "")
      val tag = params.getOrElse("tag", "")
      val regionStr = params.getOrElse("region", "EUROPE")
      val platformRegion = PlatformRegion.EUW1

      if name.isEmpty || tag.isEmpty then
        server.sendResponse(exchange, 400, """{"error":"Missing 'name' or 'tag'"}""")

      val regional = RegionalRoute.valueOf(regionStr)

      val profile = service.getSummonerProfile(regional, platformRegion, name, tag)

      val json =
        s"""{
           "name":"${profile.name()}",
           "tag":"${profile.tag()}",
           "level":"${profile.level()}"
        }"""

      server.sendResponse(exchange, 200, json)

    catch
      case e: Exception =>
        e.printStackTrace()
        server.sendResponse(exchange, 500, s"""{"error":"${e.getMessage}"}""")
  }

  server.start()
}