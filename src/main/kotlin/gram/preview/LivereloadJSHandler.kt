package gram.preview

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LivereloadJSHandler : AbstractHandler() {
    override fun handle(target: String?, baseRequest: Request?, request: HttpServletRequest?, response: HttpServletResponse?) {

        if (target == "/livereload.js" && response != null && baseRequest != null) {
            val resource = javaClass.getResource("/META-INF/resources/webjars/livereload-js/3.0.0/dist/livereload.js")
            val content = resource.readText()
            response.addHeader("Content-Length", "${content.length}")
            response.addHeader("Content-Type", "application/javascript")
            response.writer.use {
                it.print(content)
            }
            baseRequest.isHandled = true
        }
    }
}