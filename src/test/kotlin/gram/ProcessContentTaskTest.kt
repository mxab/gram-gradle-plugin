package gram

import org.asciidoctor.Asciidoctor.Factory.create
import org.junit.jupiter.api.Test

internal class ProcessContentTaskTest {

    @Test
    fun test() {
        val asciidoctor = create()
        val html = asciidoctor.convert("Writing AsciiDoc is _easy_!", emptyMap())
        println(html)
    }
}