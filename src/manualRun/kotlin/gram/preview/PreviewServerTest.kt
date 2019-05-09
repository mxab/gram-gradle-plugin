package gram.preview

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.util.resource.PathResource
import org.junit.jupiter.api.Test
import java.io.File

internal class PreviewServerTest {

    @Test
    fun server() {
        val siteDir = File("/Users/bruchmann/git/gram/demo/build/site").toPath()
        val previewServer = PreviewServer(siteDir)

        previewServer.start()

        val fileWatcher = FileWatcher(siteDir)

        Thread({
            fileWatcher.watch()
        }).start()

        previewServer.join()
    }

    @Test
    fun jetty() {
        val port = 7181
        // Create a basic Jetty server object that will listen on port 8080.  Note that if you set this to port 0
        // then a randomly available port will be assigned that you can either look in the logs for the port,
        // or programmatically obtain it for use in test cases.
        val server = Server(port)

        // Create the ResourceHandler. It is the object that will actually handle the request for a given file. It is
        // a Jetty Handler object so it is suitable for chaining with other handlers as you will see in other examples.
        val resource_handler = ResourceHandler()

        // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
        // In this example it is the current directory but it can be configured to anything that the jvm has access to.
        resource_handler.isDirectoriesListed = true
        resource_handler.welcomeFiles = arrayOf("index.html")
        resource_handler.baseResource = PathResource(File("/Users/bruchmann/git/gram/demo/build/site").toPath())

        // Add the ResourceHandler to the server.
        val handlers = HandlerList()
        handlers.handlers = arrayOf(resource_handler, DefaultHandler())
        server.handler = handlers

        // Start things up! By using the server.join() the server thread will join with the current thread.
        // See "http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()" for more details.
        server.start()

        val previewUrl = "http://localhost:$port"

        server.join()
    }
}