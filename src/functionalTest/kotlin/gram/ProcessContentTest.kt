package gram

import org.assertj.core.api.Assertions
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

    @BeforeEach
    fun setup() {

        settingsFile = File(temp, "settings.gradle")
        buildFile = File(temp, "build.gradle")

        val contentDir = File(temp, "src/content")
        contentDir.mkdirs()

        File(temp, "src/templates").mkdirs()
        File(contentDir, "index.adoc").writeText("""
            = Hello World

            == Foo
        """.trimIndent())
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
        val build = GradleRunner.create()
                .withProjectDir(temp)
                .withArguments("processContent")
                .withPluginClasspath()
                .build()

        val task = build.task(":processContent")
        Assertions.assertThat(task)
                .isNotNull
                .hasFieldOrPropertyWithValue("outcome", TaskOutcome.SUCCESS)

        val doc = Jsoup.parse(File(temp, "build/content/index.html").readText())

        assertThat(doc.select("#header h1").text()).isEqualTo("Hello World")
        assertThat(doc.select("#content h2").text()).isEqualTo("Foo")
    }
}