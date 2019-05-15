package gram.preview

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.websocket.server.WebSocketHandler
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory
import java.nio.file.Path

class PreviewServer(siteDir: Path, contextPath: String = "/") : Server() {

    public val port = 7181

    private val rootContextPath = "/"
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
        val livereloadJSHandler = LivereloadJSHandler()

        val contextCollection = ContextHandlerCollection()

        val contextWithPath = ContextHandler()
        contextWithPath.contextPath = contextPath

        val siteDirHandler = SiteDirHandler(siteDir)

        val handlersForContextWithPath = HandlerList()
        contextWithPath.handler = handlersForContextWithPath

        if (rootContextPath == contextPath) { // use one context
            handlersForContextWithPath.handlers = arrayOf(wsHandler, livereloadJSHandler, siteDirHandler, DefaultHandler())

            contextCollection.handlers = arrayOf(contextWithPath)
        } else {

            handlersForContextWithPath.handlers = arrayOf(siteDirHandler, DefaultHandler())
            val livereloadWSContext = createLivereloadContext(wsHandler, livereloadJSHandler)
            contextCollection.handlers = arrayOf(contextWithPath, livereloadWSContext)
        }

        this.handler = contextCollection
    }

    private fun createLivereloadContext(wsHandler: WebSocketHandler, livereloadJSHandler: LivereloadJSHandler): ContextHandler {
        val livereloadWSContext = ContextHandler()
        livereloadWSContext.contextPath = "/"
        livereloadWSContext.handler = HandlerList(wsHandler, livereloadJSHandler)
        return livereloadWSContext
    }
}
