package gram.preview

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import java.nio.file.Files
import java.nio.file.Path
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SiteDirHandler(private val siteDir: Path) : AbstractHandler() {

    override fun handle(target: String?, baseRequest: Request?, request: HttpServletRequest?, response: HttpServletResponse?) {

        if (target != null && response != null && baseRequest != null) {
            var file = target
            if (file.endsWith("/")) {
                file = target + "index.html"
            }
            file = file.removePrefix("/")

            val path = siteDir.resolve(file)
            val toFile = path.toFile()
            if (toFile.exists()) {

                if (path.fileName.toString().endsWith(".html")) {
                    var html = toFile.readText()
                    val liveReloadScriptTag = """<script src="/livereload.js"></script>"""
                    html = if (html.contains("</body>", true)) {
                        html.replace("</body>", """$liveReloadScriptTag</body>""", true)
                    } else {
                        html.plus(liveReloadScriptTag)
                    }
                    val length = html.length.toLong()
                    response.addHeader("Content-Length", "$length")
                    response.writer.use {
                        it.print(html)
                    }
                } else {
                    response.addHeader("Content-Length", "${toFile.length()}")
                    response.outputStream.use {
                        Files.copy(path, it)
                    }
                }
                baseRequest.isHandled = true
            }
        }
    }
}