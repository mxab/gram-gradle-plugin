package gram.preview

import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter

class LivereloadWebSocketHandler : WebSocketAdapter() {

    companion object {
        var handlers = emptyList<LivereloadWebSocketHandler>()
        fun reloadPath(path: String) {
            handlers.forEach({ h -> h.reloadPath(path) })
        }
    }

    private val objectMapper = ObjectMapper()

    override fun onWebSocketConnect(sess: Session) {
        super.onWebSocketConnect(sess)

        handlers = handlers.plus(this)
        println("Socket Connected: $sess")
    }

    override fun onWebSocketText(text: String?) {

        val message = objectMapper.readTree(text)
        val command = message.get("command")

        if (command.asText().toLowerCase().equals("hello")) {
            remote.sendString("""
                {
                    "command":"hello",
                    "protocols":["http://livereload.com/protocols/official-7"],
                    "serverName":"gram preview server"
                }
                """.trimIndent())
        }
    }

    override fun onWebSocketClose(statusCode: Int, reason: String?) {
        super.onWebSocketClose(statusCode, reason)
        handlers = handlers.minus(this)
    }

    override fun onWebSocketError(cause: Throwable?) {
        super.onWebSocketError(cause)
        cause!!.printStackTrace(System.err)
    }

    fun reloadPath(file: String) {
        remote.sendString("""{"command":"reload","path":"$file","liveCss":true}""")
    }
}