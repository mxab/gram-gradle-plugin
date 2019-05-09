package gram

import gram.preview.PreviewTask
import org.assertj.core.api.Assertions.assertThat
import org.gradle.api.tasks.Copy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Files.createDirectories
import java.nio.file.Files.createFile
import java.nio.file.Path
import org.gradle.testfixtures.ProjectBuilder.builder as projectBuilder

internal class GramPluginTest {

    @Test
    fun appliesBasePlugin() {
        val project = projectBuilder()
                .build()
        project.pluginManager.apply(GramPlugin::class.java)
        assertThat(project.pluginManager.hasPlugin("base")).isTrue()
    }

    @Test
    fun test(@TempDir tempDir: Path) {

        val projectDir = Files.createDirectory(tempDir.resolve("project"))

        val pages = projectDir.resolve("src/content")
        createDirectories(pages)

        page(pages, "index.adoc", """
                = Hello, AsciiDoc!
                Doc Writer <doc@example.com>
                """.trimIndent())

        page(pages, "about.adoc", """
                == First Section

                * item 1
                * item 2""".trimIndent())

        val project = projectBuilder()
                .withProjectDir(projectDir.toFile())
                .build()
        project.pluginManager.apply(GramPlugin::class.java)

        assertThat(project.tasks.findByName("processContent")).isInstanceOf(ProcessContentTask::class.java)
    }

    private fun page(pages: Path, s: String, fileContent: String) {
        val index = pages.resolve(s)
        createFile(index)
        index.toFile().printWriter().use { out ->
            out.println(fileContent)
        }
    }

    @Test
    fun ext() {
        val project = projectBuilder().build()
        project.pluginManager.apply(GramPlugin::class.java)
        val byType = project.extensions.getByType(GramExtension::class.java)
        assertThat(byType).isNotNull
    }

    @Test
    fun tyhemleaf() {
        val project = projectBuilder().build()
        project.pluginManager.apply(GramPlugin::class.java)
        assertThat(project.tasks.findByName("generateHTML")).isInstanceOf(GenerateHTMLTask::class.java)
    }

    @Test
    fun staticTask() {
        val project = projectBuilder().build()
        project.pluginManager.apply(GramPlugin::class.java)
        assertThat(project.tasks.findByName("processStatics")).isInstanceOf(Copy::class.java)
    }

    @Test
    fun siteTask() {
        val project = projectBuilder().build()
        project.pluginManager.apply(GramPlugin::class.java)
        assertThat(project.tasks.findByName("site")).isInstanceOf(Copy::class.java)
    }

    @Test
    fun previewTask() {
        val project = projectBuilder().build()
        project.pluginManager.apply(GramPlugin::class.java)
        assertThat(project.tasks.findByName("preview")).isInstanceOf(PreviewTask::class.java)
    }
}