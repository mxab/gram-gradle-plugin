package gram

import gram.extension.GoogleMapsInlineMacroProcessor
import java.nio.file.Files
import java.nio.file.Path
import org.asciidoctor.Asciidoctor.Factory.create
import org.asciidoctor.OptionsBuilder.options
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

open class ProcessContentTask : DefaultTask() {

    @InputDirectory
    lateinit var contentDir: Path
    @OutputDirectory
    lateinit var outputDir: Path

    @TaskAction
    fun process() {

        val asciidoctor = create()
        val javaExtensionRegistry = asciidoctor.javaExtensionRegistry()
        javaExtensionRegistry
                    .inlineMacro(GoogleMapsInlineMacroProcessor::class.java)

        val options = options()
                .toFile(false)
                .headerFooter(true)
                .asMap()

        project
                .fileTree(contentDir)
                .visit {

                    if (name.endsWith(".adoc")) {
                        val html = asciidoctor.convertFile(file, options)
                        val pageOutput = outputDir
                                .resolve(relativePath.pathString)
                                .resolveSibling(file.name.removeSuffix(".adoc").plus(".html"))
                        Files.createDirectories(pageOutput.parent)
                        pageOutput.toFile()
                                .writeText(html)
                    }
                }
    }
}
