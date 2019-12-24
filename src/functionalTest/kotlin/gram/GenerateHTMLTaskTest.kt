package gram

import java.io.File
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.jsoup.Jsoup
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

internal class GenerateHTMLTaskTest {

    @TempDir
    lateinit var temp: File
    lateinit var settingsFile: File

    @BeforeEach
    fun setup() {

        settingsFile = File(temp, "settings.gradle")
        settingsFile.writeText("""
            rootProject.name = "test-generate-html"
        """.trimIndent())
    }

    @Test
    fun test() {

        val buildFile = File(temp, "build.gradle")

        val templatesDir = File(temp, "src/templates")
        templatesDir.mkdirs()

        File(temp, "src/content").mkdirs()
        val contentBuildDir = File(temp, "build/content_replacement")
        contentBuildDir.mkdirs()

        val outputDir = File(temp, "build/pages")

        buildFile.writeText("""
            plugins {
                id "io.github.mxab.gram"
            }

            tasks.named("generateHTML").configure {
                contentHtmlDir = file("${contentBuildDir.path}").toPath()
                templatesDir = file("${templatesDir.path}").toPath()
                outputDir = file("${outputDir.path}").toPath()
            }
        """.trimIndent())

        val aboutHtml = File(contentBuildDir, "about.html")
        aboutHtml.writeText("""
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                </head>
                <body>
                    <div id="header"><h1>Hello</h1></div>
                </body>
            </html>
        """.trimIndent())

        val defaultTemplate = File(templatesDir, "default.html")
        defaultTemplate.writeText("""
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                </head>
                <body>
                <header th:insert="${'$'}{content} :: #header"></header>
                </body>
            </html>
        """.trimIndent())

        val build = GradleRunner.create()
                .withProjectDir(temp)
                .withArguments("generateHTML")
                .withDebug(true)
                .withPluginClasspath()
                .build()

        val task = build.task(":generateHTML")
        assertThat(task)
                .isNotNull
                .hasFieldOrPropertyWithValue("outcome", TaskOutcome.SUCCESS)

        val doc = Jsoup.parse(File(outputDir, "about/index.html").readText())
        assertThat(doc.select("header #header h1").text())
                .isEqualTo("Hello")
    }

    @Test
    fun test_with_sub_pages() {

        val buildFile = File(temp, "build.gradle")

        val templatesDir = File(temp, "src/templates")
        templatesDir.mkdirs()

        File(temp, "src/content").mkdirs()
        val contentBuildDir = File(temp, "build/content_replacement")
        contentBuildDir.mkdirs()

        val outputDir = File(temp, "build/pages")

        buildFile.writeText("""
            plugins {
                id "io.github.mxab.gram"
            }

            tasks.named("generateHTML").configure {
                contentHtmlDir = file("${contentBuildDir.path}").toPath()
                templatesDir = file("${templatesDir.path}").toPath()
                outputDir = file("${outputDir.path}").toPath()
            }
        """.trimIndent())

        val sub = File(contentBuildDir, "more")
        sub.mkdirs()
        val aboutHtml = File(sub, "about.html")
        aboutHtml.writeText("""
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                </head>
                <body>
                    <div id="header"><h1>Hello</h1></div>
                </body>
            </html>
        """.trimIndent())

        val defaultTemplate = File(templatesDir, "default.html")
        defaultTemplate.writeText("""
            <!DOCTYPE html>
            <html lang="en">
                <head>
                    <meta charset="UTF-8">
                </head>
                <body>
                <header th:insert="${'$'}{content} :: #header"></header>
                </body>
            </html>
        """.trimIndent())

        val build = GradleRunner.create()
                .withProjectDir(temp)
                .withArguments("generateHTML")
                .withDebug(true)
                .withPluginClasspath()
                .build()

        val task = build.task(":generateHTML")
        assertThat(task)
                .isNotNull
                .hasFieldOrPropertyWithValue("outcome", TaskOutcome.SUCCESS)

        val doc = Jsoup.parse(File(outputDir, "more/about/index.html").readText())
        assertThat(doc.select("header #header h1").text())
                .isEqualTo("Hello")
    }
}
