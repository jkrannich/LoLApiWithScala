import core.RiotApi
import core.config.Regions.{PlatformRegion, RegionalRoute}
import core.config.RiotApiConfig
import core.http.JavaNetRiotHttp
import io.github.cdimascio.dotenv.Dotenv

import java.time.Duration

@main
def main(): Unit = {
  val dotenv = Dotenv.load()
  val apiKey = dotenv.get("RIOT_API_KEY")

  val config = new RiotApiConfig(apiKey, PlatformRegion.EUW1, RegionalRoute.EUROPE, Duration.ofSeconds(10))

  val riotHttp = new JavaNetRiotHttp(config)
  val riotApi = new RiotApi(riotHttp)

  val account = riotApi.account().byRiotId(RegionalRoute.EUROPE, "Thayger", "Soul")
  println(account)
}

