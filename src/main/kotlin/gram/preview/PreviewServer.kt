package gram.preview

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.websocket.server.WebSocketHandler
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory
import java.nio.file.Path
import org.eclipse.jetty.server.handler.ContextHandlerCollection

class PreviewServer(siteDir: Path, contextPath: String = "/") : Server() {

    public val port = 7181

    init {
        val http = ServerConnector(this)
        http.host = "localhost"
        http.port = port

        http.idleTimeout = 30000

        // Set the connector
        this.addConnector(http)

        val http2 = ServerConnector(this)
        http2.host = "localhost"
        http2.port = 35729
        http2.idleTimeout = 30000
        // Set the connector
        this.addConnector(http2)
        val wsHandler = object : WebSocketHandler() {
            override fun configure(factory: WebSocketServletFactory) {
                factory.register(LivereloadWebSocketHandler::class.java)
            }
        }
        val livereloadWSContext = ContextHandler()
        livereloadWSContext.contextPath = "/"
        val livereloadJSHandler = LivereloadJSHandler()
        livereloadWSContext.handler = HandlerList(wsHandler, livereloadJSHandler)

        val contextWithPath = ContextHandler()
        contextWithPath.contextPath = contextPath

        val siteDirHandler = SiteDirHandler(siteDir)
        val handlers = HandlerList()
        handlers.handlers = arrayOf(siteDirHandler, DefaultHandler())
        contextWithPath.handler = handlers

        val contexts = ContextHandlerCollection()
        contexts.handlers = arrayOf(contextWithPath, livereloadWSContext)

        this.handler = contexts
    }
}
