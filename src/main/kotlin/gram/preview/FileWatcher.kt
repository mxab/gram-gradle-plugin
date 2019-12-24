package gram.preview

import java.nio.file.ClosedWatchServiceException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchEvent

class FileWatcher(private val rootDir: Path) {

    fun watch() {
        val watchService = FileSystems
                .getDefault()
                .newWatchService()

        rootDir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY)

        do {
            var run = true
            try {
                val key = watchService.take()
                for (event in key.pollEvents()) {

                    val context = (event as WatchEvent<*>).context()
                    LivereloadWebSocketHandler.reloadPath("/$context")
                }
                key.reset()
            } catch (e: ClosedWatchServiceException) {
                e.printStackTrace()
                run = false
            }
        } while (run)
    }
}
