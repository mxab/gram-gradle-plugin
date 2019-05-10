package gram.preview

import org.junit.jupiter.api.Test
import java.io.File

internal class PreviewServerTest {

    @Test
    fun server() {
        val siteDir = File(System.getProperty("gram.demo")).toPath()
        val previewServer = PreviewServer(siteDir)

        previewServer.start()

        val fileWatcher = FileWatcher(siteDir)

        Thread({
            fileWatcher.watch()
        }).start()

        previewServer.join()
    }
}