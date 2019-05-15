package gram

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.thymeleaf.IEngineConfiguration
import org.thymeleaf.context.ExpressionContext

internal class GramLinkBuilderTest {

    @Test
    fun uses_slash_as_default() {
        val gramLinkBuilder = GramLinkBuilder()
        val conf = Mockito.mock(IEngineConfiguration::class.java)
        val link = gramLinkBuilder.buildLink(ExpressionContext(conf), "/foo/bar", emptyMap())
        assertThat(link).isEqualTo("/foo/bar")
    }
    @Test
    fun with_url() {
        val gramLinkBuilder = GramLinkBuilder()
        val conf = Mockito.mock(IEngineConfiguration::class.java)
        val link = gramLinkBuilder.buildLink(ExpressionContext(conf), "http://example.org/foo/bar", emptyMap())
        assertThat(link).isEqualTo("http://example.org/foo/bar")
    }
    @Test
    fun uses_given_context_as_default() {
        val gramLinkBuilder = GramLinkBuilder("/gram")
        val conf = Mockito.mock(IEngineConfiguration::class.java)
        val link = gramLinkBuilder.buildLink(ExpressionContext(conf), "/foo/bar", emptyMap())
        assertThat(link).isEqualTo("/gram/foo/bar")
    }
}