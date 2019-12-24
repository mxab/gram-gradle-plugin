package gram

import org.junit.jupiter.api.Test
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver

internal class ThymeleafTest {

    @Test
    fun thyme() {
        val templateResolver = FileTemplateResolver()

        // HTML is the default mode, but we set it anyway for better understanding of code
        templateResolver.setTemplateMode(TemplateMode.HTML)
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix("src/test/resources/templates/")
        templateResolver.setSuffix(".html")
        // Template cache TTL=1h. If not set, entries would be cached until expelled
        templateResolver.setCacheTTLMs(java.lang.Long.valueOf(3600000L))

        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        templateResolver.setCacheable(true)

        val templateEngine = TemplateEngine()
        templateEngine.setTemplateResolver(templateResolver)
        val context = Context()

        val process = templateEngine.process("test", context)

        println(process)
    }
}
