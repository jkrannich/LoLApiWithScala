import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}

import java.net.{InetSocketAddress, URLDecoder}
import java.nio.charset.StandardCharsets

class SimpleHttpServer(port: Int):

  private val server: HttpServer = HttpServer.create(new InetSocketAddress(port), 0)

  def get(path: String)(handler: HttpExchange => Unit): Unit =
    server.createContext(path, (exchange: HttpExchange) => if exchange.getRequestMethod == "GET" then
      handler(exchange)
    else
      sendResponse(exchange, 405, "Method not allowed")
    )

  def start(): Unit = {
    server.setExecutor(null)
    server.start()
    println(s"Http server running at http://localhost:$port")
  }

  def sendResponse(exchange: HttpExchange, status: Int, body: String): Unit =
    val bytes = body.getBytes(StandardCharsets.UTF_8)
    exchange.getResponseHeaders.add("Content-Type", "application/json")
    exchange.getResponseHeaders.add("Access-Control-Allowed-Origin", "*")
    exchange.sendResponseHeaders(status, bytes.length)
    val os = exchange.getResponseBody
    os.write(bytes)
    os.close()

  def queryParams(exchange: HttpExchange): Map[String, String] =
    val query = Option(exchange.getRequestURI.getQuery).getOrElse("")
    query.split("&")
      .filter(_.contains("="))
      .map { pair =>
        val Array(k, v) = pair.split("=", 2)
        k -> URLDecoder.decode(v, "UTF-8")
      }
      .toMap


