package gram

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jsoup.Jsoup
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

internal class ProcessContentTest {

    @TempDir
    lateinit var temp: File
    lateinit var settingsFile: File
    lateinit var buildFile: File

    lateinit var contentDir: File
    @BeforeEach
    fun setup() {

        settingsFile = File(temp, "settings.gradle")
        buildFile = File(temp, "build.gradle")

        contentDir = File(temp, "src/content")
        contentDir.mkdirs()

        File(temp, "src/templates").mkdirs()

        settingsFile.writeText("""
            rootProject.name = "test-process-html"
        """.trimIndent())

        buildFile.writeText("""
            plugins {
                id "io.github.mxab.gram"
            }
        """.trimIndent())
    }

    @Test
    fun test() {

        File(contentDir, "index.adoc").writeText("""
            = Hello World

            == Foo
        """.trimIndent())

        runProcessContent()

        val doc = Jsoup.parse(File(temp, "build/content/index.html").readText())

        assertThat(doc.select("#header h1").text()).isEqualTo("Hello World")
        assertThat(doc.select("#content h2").text()).isEqualTo("Foo")
    }

    @Test
    fun test_sub_dir() {

        val subContentDir = File(contentDir, "sub")
        subContentDir.mkdirs()
        File(subContentDir, "foo.adoc").writeText("""
            = Foo

            == Sub
        """.trimIndent())

        runProcessContent()

        val subDoc = Jsoup.parse(File(temp, "build/content/sub/foo.html").readText())

        assertThat(subDoc.select("#header h1").text()).isEqualTo("Foo")
        assertThat(subDoc.select("#content h2").text()).isEqualTo("Sub")
    }

    private fun runProcessContent() {
        val build = GradleRunner.create()
                .withProjectDir(temp)
                .withArguments("processContent")
                .withPluginClasspath()
                .withDebug(true)
                .build()

        val task = build.task(":processContent")
        assertThat(task)
                .isNotNull
                .hasFieldOrPropertyWithValue("outcome", TaskOutcome.SUCCESS)
    }
}