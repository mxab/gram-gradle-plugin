package gram.extension

import org.asciidoctor.ast.ContentNode
import org.asciidoctor.extension.InlineMacroProcessor
import org.asciidoctor.extension.Name

@Name("gmaps")
class GoogleMapsInlineMacroProcessor : InlineMacroProcessor() {

    override fun process(parent: ContentNode?, target: String?, attributes: MutableMap<String, Any>?): Any {

        return """
            <iframe 
            src="$target" width="600" height="450" frameborder="0" style="border:0;" allowfullscreen="">
            </iframe>
        """.trimIndent()
    }
}
