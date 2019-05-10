package gram.preview

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.DefaultHandler
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.PathResource
import java.io.File

open class PreviewTask : DefaultTask() {

    lateinit var siteDir: File

    private val port = 7181

    @TaskAction
    fun preview() {
        val server = PreviewServer(siteDir.toPath())

        server.start()
        val previewUrl = "http://localhost:$port"
        logger.lifecycle("Started preview server at $previewUrl")

        server.join()
    }
}