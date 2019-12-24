package gram

import org.thymeleaf.context.IExpressionContext
import org.thymeleaf.linkbuilder.StandardLinkBuilder

class GramLinkBuilder(private val contextPath: String = "/") : StandardLinkBuilder() {

    override fun computeContextPath(context: IExpressionContext?, base: String?, parameters: MutableMap<String, Any>?): String {
        return contextPath
    }
}
