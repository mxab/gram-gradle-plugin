package gram.preview

import java.io.File
import org.junit.jupiter.api.Test

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
