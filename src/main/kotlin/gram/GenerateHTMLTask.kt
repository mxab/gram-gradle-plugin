package gram

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode.HTML
import org.thymeleaf.templateresolver.FileTemplateResolver
import java.nio.file.Path

open class GenerateHTMLTask : DefaultTask() {

    @InputDirectory
    lateinit var templatesDir: Path

    @InputDirectory
    lateinit var contentHtmlDir: Path

    @Input
    lateinit var contextPath: String

    @OutputDirectory
    lateinit var outputDir: Path

    @TaskAction
    fun generate() {

        val templateResolver = FileTemplateResolver()

        // HTML is the default mode, but we set it anyway for better understanding of code
        templateResolver.templateMode = HTML
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.prefix = "$templatesDir/"
        templateResolver.suffix = ".html"
        // Template cache TTL=1h. If not set, entries would be cached until expelled
        templateResolver.cacheTTLMs = java.lang.Long.valueOf(3600000L)

        // Cache is set to true by default. Set to false if you want templates to
        // be automatically updated when modified.
        templateResolver.isCacheable = true

        val templateEngine = TemplateEngine()
        templateEngine.setTemplateResolver(templateResolver)
        templateEngine.setLinkBuilder(GramLinkBuilder(contextPath))

        project.fileTree(contentHtmlDir)
                .visit({

                    if (!file.isDirectory) {

                        val relativize = templatesDir.relativize(file.toPath())

                        val context = Context()

                        context.setVariable("content", relativize.toString().removeSuffix(".html"))

                        val process = templateEngine.process("default", context)

                        var output = relativePath
                                .pathString

                        if (!output.endsWith("index.html")) {
                            output = output.removeSuffix(".html").plus("/index.html")
                        }
                        val outputFile = outputDir
                                .resolve(output)
                                .toFile()

                        outputFile.parentFile.mkdirs()
                        outputFile.writeText(process)
                    }
                })
    }
}
