package gram.preview

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.jetty.websocket.api.RemoteEndpoint
import org.eclipse.jetty.websocket.api.Session
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*

internal class LivereloadWebSocketHandlerTest {
    val objectMapper = ObjectMapper()
    val handler = LivereloadWebSocketHandler()
    val remoteEndpoint = mock(RemoteEndpoint::class.java)
    val session: Session = mock(Session::class.java)

    @BeforeEach
    fun setup() {

        Mockito.`when`(session.remote).thenReturn(remoteEndpoint)
        handler.onWebSocketConnect(session)
    }

    @Test
    fun says_hello_on_hello() {
        val helloObj = objectMapper.createObjectNode()
        helloObj.put("command", "hello")

        val protoocolsNode = helloObj.putArray("protocols")
        listOf("http://livereload.com/protocols/official-7").forEach(protoocolsNode::add)
        helloObj.put("serverName", "LiveReload 2")

        val message = objectMapper.writeValueAsString(helloObj)
        handler.onWebSocketText(message)

        var captor = ArgumentCaptor.forClass(String::class.java)
        verify(remoteEndpoint).sendString(captor.capture())

        val toJsonTree = objectMapper.readTree(captor.value)

        assertThat(toJsonTree.get("command").asText()).isEqualTo("hello")
        assertThat(toJsonTree.get("protocols").isArray).isTrue()
        assertThat(toJsonTree.get("protocols")[0].asText()).contains("http://livereload.com/protocols/official-7")
        assertThat(toJsonTree.get("serverName").asText()).isEqualTo("gram preview server")
    }

    @Test
    fun do_nothing_on_other_command() {
        val createObjectNode = objectMapper.createObjectNode()
        createObjectNode.put("command", "foo")
        val message = objectMapper.writeValueAsString(createObjectNode)
        handler.onWebSocketText(message)
        verify(remoteEndpoint, never()).sendString(ArgumentMatchers.anyString())
    }

    @Test
    fun notifiy() {
        handler.reloadPath(("foo.txt"))

        var captor = ArgumentCaptor.forClass(String::class.java)
        verify(remoteEndpoint).sendString(captor.capture())

        val toJsonTree = objectMapper.readTree(captor.value)

        assertThat(toJsonTree.get("command").asText()).isEqualTo("reload")

        assertThat(toJsonTree.get("path").asText()).isEqualTo("/foo.txt")
        assertThat(toJsonTree.get("liveCss").asBoolean()).isTrue()
    }
}