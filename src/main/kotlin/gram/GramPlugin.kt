package gram

import gram.preview.PreviewTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Copy
import org.gradle.kotlin.dsl.create

class GramPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.pluginManager.apply(BasePlugin::class.java)

        val extension = project.extensions.create<GramExtension>("gram")

        val pagesSrcDir = project.file("src/content")
        val processContentTask = project.tasks.register("processContent", ProcessContentTask::class.java) {
            group = "gram"
            contentDir = pagesSrcDir.toPath()
            outputDir = project.buildDir.toPath().resolve("content")
        }

        val generateHTML = project.tasks.register("generateHTML", GenerateHTMLTask::class.java) {
            group = "gram"
            templatesDir = project.file("src/templates").toPath()
            outputDir = project.buildDir.toPath().resolve("pages")
            dependsOn(processContentTask)
            contentHtmlDir = processContentTask.get().outputDir
            contextPath = extension.contextPath
        }

        val processStatic = project.tasks.register("processStatic", Copy::class.java) {

            from("src/static")
            into(project.buildDir.toPath().resolve("static"))
        }
        val siteTask = project.tasks.register("site", Copy::class.java) {

            from(generateHTML)
            from(processStatic)
            into(project.buildDir.toPath().resolve("site"))
        }

        project.tasks.register("preview", PreviewTask::class.java) {
            siteDir = siteTask.get().destinationDir
            contextPath = extension.contextPath
        }
    }
}