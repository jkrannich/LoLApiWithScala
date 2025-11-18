import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

class SimpleHttpServer(port: Int):

  private val server: HttpServer = HttpServer.create(new InetSocketAddress(port), 0)

  def get(path: String)(handler: HttpExchange => Unit): Unit =
    server.createContext(path, new HttpHandler:
      override def handle(exchange: HttpExchange): Unit =
        if exchange.getRequestMethod == "GET" then
          handler(exchange)
        else
          sendResponse(exchange, 405, "Method not allowed")
    )

  private def sendResponse(exchange: HttpExchange, status: Int, body: String): Unit =
    val bytes = body.getBytes(StandardCharsets.UTF_8)
    exchange.getResponseHeaders.add("Content-Type", "application/json")
    exchange.sendResponseHeaders(status, bytes.length)
    val os = exchange.getResponseBody
    os.write(bytes)
    os.close()


