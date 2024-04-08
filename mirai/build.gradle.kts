import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly
import java.io.ByteArrayOutputStream

plugins {
    val kotlinVersion = "1.9.23"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    //id("com.github.gmazzo.buildconfig") version "3.1.0" //什么猪鼻插件
    id("net.mamoe.mirai-console") version "2.16.0" //什么猪鼻插件
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven ("https://libraries.minecraft.net")
}


kotlin {
    jvmToolchain(17)
}

mirai {
    noTestCore = true
    setupConsoleTestRuntime {
        classpath = classpath.filter {
            !it.nameWithoutExtension.startsWith("mirai-core-jvm")
        }
    }
}

dependencies {
    val overflowVersion = "2.16.0-695e4e1-SNAPSHOT"
    compileOnly("org.slf4j:slf4j-api:1.7.36")
    compileOnly("top.mrxiaom:overflow-core-api:$overflowVersion")
    testConsoleRuntime("top.mrxiaom:overflow-core:$overflowVersion")
    compileOnly("net.mamoe:mirai-console:2.16.0")
    compileOnly("net.mamoe:mirai-core:2.16.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.mojang:brigadier:1.0.18")
    implementation("nl.vv32.rcon:rcon:1.2.0")
    implementation("io.ktor:ktor-client-cio:2.3.9")
    implementation("io.ktor:ktor-client-auth:2.3.9")
    implementation("io.ktor:ktor-client-serialization:2.3.9")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.9")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.9")
}

tasks.withType<ShadowJar>(){
    this.exclude("/kotlin*", "/net/mamoe/*")
}

task("generateProperties") {
    doLast {
        generateProperties()
    }
}

tasks.getByName("processResources") {
    dependsOn("generateProperties")
}

fun getGitBranch(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "symbolic-ref", "--short", "-q", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString(Charsets.UTF_8).trim()
}

fun getCommitId(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString(Charsets.UTF_8).trim()
}

fun generateProperties() {
    val propertiesFile = file("./src/main/resources/build.properties")
    if (propertiesFile.exists()) {
        propertiesFile.delete()
    }
    propertiesFile.createNewFile()
    val m = mutableMapOf<String, String>()
    propertiesFile.printWriter().use { writer ->
        properties.forEach {
            val str = it.value.toString()
            if ("@" in str || "(" in str || ")" in str || "extension" in str || "null" == str || "\'" in str || "\\" in str || "/" in str) return@forEach
            if ("PROJECT" in str.toUpperCaseAsciiOnly() || "PROJECT" in it.key.toUpperCaseAsciiOnly() || " " in str) return@forEach
            if ("GRADLE" in it.key.toUpperCaseAsciiOnly() || "GRADLE" in str.toUpperCaseAsciiOnly() || "PROP" in it.key.toUpperCaseAsciiOnly()) return@forEach
            if ("." in it.key || "TEST" in it.key.toUpperCaseAsciiOnly()) return@forEach
            if (it.value.toString().length <= 2) return@forEach
            m += it.key to str
        }
        m += "buildTime" to System.currentTimeMillis().toString()
        m += "branch" to getGitBranch()
        m += "commitId" to getCommitId()
        m.toSortedMap().forEach {
            writer.println("${it.key} = ${it.value}")
        }
    }
}