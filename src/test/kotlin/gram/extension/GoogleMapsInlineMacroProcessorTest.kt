package gram.extension

import java.io.File
import org.asciidoctor.Asciidoctor
import org.asciidoctor.OptionsBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

internal class GoogleMapsInlineMacroProcessorTest {
    @Test
    fun process(@TempDir dir: File) {
        val asciidoctor = Asciidoctor.Factory.create()

        asciidoctor.javaExtensionRegistry().inlineMacro(GoogleMapsInlineMacroProcessor::class.java) // (1)

        // https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d5371.632333890272!2d9.76023!3d47.68799!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x7114774153d57225!2sBiohof+Schauwies!5e0!3m2!1sde!2sde!4v1563703209758!5m2!1sde!2sdes
        val result = asciidoctor.convert("""
            I live here:
            
            gmaps:https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d690.6144467234172!2d9.760455746165123!3d47.68798967132383!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x7114774153d57225!2sBiohof%20Schauwies!5e1!3m2!1sde!2sde!4v1568540122419!5m2!1sde!2sde[]
            
        """.trimIndent(), OptionsBuilder.options()
                .headerFooter(false)
                .get())

        assertThat(result).contains("<iframe")
        assertThat(result).contains("https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d690.6144467234172!2d9.760455746165123!3d47.68798967132383!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x7114774153d57225!2sBiohof%20Schauwies!5e1!3m2!1sde!2sde!4v1568540122419!5m2!1sde!2sde")
    }
}
