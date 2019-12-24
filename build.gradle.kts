import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {

    kotlin("jvm") version "1.3.50"
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.10.1"
    id("org.jmailen.kotlinter") version "2.2.0"
}


tasks {
    named<Wrapper>("wrapper") {
        gradleVersion = "6.0.1"
    }
}
sourceSets {
    create("functionalTest") {
        compileClasspath += sourceSets.main.get().output + configurations.testRuntimeClasspath
        runtimeClasspath += output + compileClasspath
    }
    create("manualRun") {
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath + runtimeClasspath
    }
}
gradlePlugin {
    plugins {
        create("gramPlugin") {
            id = "io.github.mxab.gram"
            implementationClass = "gram.GramPlugin"
        }
    }
    testSourceSets(sourceSets["functionalTest"])
}
tasks.register<Test>("functionalTest") {
    testClassesDirs = sourceSets["functionalTest"].output.classesDirs
    classpath = sourceSets["functionalTest"].runtimeClasspath
}

tasks.check { dependsOn(tasks["functionalTest"]) }


configurations {
    "manualRunImplementation" {
        extendsFrom(configurations["testImplementation"])
    }
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    implementation("org.thymeleaf:thymeleaf:3.0.11.RELEASE")
    implementation("org.asciidoctor:asciidoctorj:2.2.0")
    implementation("org.eclipse.jetty:jetty-server:9.4.18.v20190429")
    implementation("org.eclipse.jetty.websocket:websocket-server:9.4.18.v20190429")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.+")
    implementation("com.fasterxml.jackson.core:jackson-core:2.9.+")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.9.+")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
    implementation("org.webjars.npm:livereload-js:3.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testImplementation("org.assertj:assertj-core:3.14.0")
    testImplementation("org.mockito:mockito-core:2.+")

    "functionalTestImplementation"("org.jsoup:jsoup:1.11.3")
    "functionalTestImplementation"("org.assertj:assertj-core:3.12.2")
}
repositories {
    jcenter()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

pluginBundle {
    // These settings are set for the whole plugin bundle
    website = "https://mxab.github.io/gram"
    vcsUrl = "https://github.com/mxab/gram-gradle-plugin"

    // tags and description can be set for the whole bundle here, but can also
    // be set / overridden in the config for specific plugins
    description = "static site generator based on gradle, thymeleaf and asciidoctor"

    (plugins) {

        // first plugin
        "gramPlugin" {
            // id is captured from java-gradle-plugin configuration
            displayName = "gram - static site generator"
            tags = listOf("static site generator", "thymeleaf", "asciidoc", "asciidoctor")
        }
    }
}

