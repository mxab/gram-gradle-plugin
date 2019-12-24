package gram.preview

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class PreviewTask : DefaultTask() {

    @Input
    lateinit var contextPath: String
    lateinit var siteDir: File

    private val port = 7181

    @TaskAction
    fun preview() {
        val server = PreviewServer(siteDir.toPath(), contextPath)

        server.start()
        val previewUrl = "http://localhost:$port$contextPath"
        logger.lifecycle("Started preview server at $previewUrl")

        val fileWatcher = FileWatcher(siteDir.toPath())
        Thread({
            fileWatcher.watch()
        }).start()

        server.join()
    }
}
