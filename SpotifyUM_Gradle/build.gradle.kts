import org.gradle.process.ExecOperations
import javax.inject.Inject

plugins {
    id("java")
    id("application")
    id("jacoco")
    id("info.solidsoft.pitest") version "1.9.0"
}

group = "org.spotifumtp37"
version = "1.0-SNAPSHOT"

jacoco {
    toolVersion = "0.8.12"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(files("lib/evosuite/evosuite-standalone-runtime-1.0.6.jar"))
    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.11.0")
    testImplementation("net.jqwik:jqwik:1.8.2")
}

sourceSets {
    val test by getting {
        java.srcDir("src/evosuite-tests")
        java.srcDir("src/hypothesis-tests")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClass.set("org.spotifumtp37.Main")
}

tasks.test {
    useJUnitPlatform()

    exclude("**/*_ESTest*")

    testLogging {
        events("failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    finalizedBy(tasks.jacocoTestReport) 
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    executionData.setFrom(fileTree(layout.buildDirectory).include("/jacoco/test.exec"))

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco"))
    }
}

tasks.register<Test>("testEvoSuite") {
    group = "verification"
    useJUnit()
    include("**/*_ESTest*")
    testLogging {
        events("passed", "skipped", "failed")
    }
}

abstract class EvoSuiteGenerateAllTask @Inject constructor(private val execOperations: ExecOperations) : DefaultTask() {
    @TaskAction
    fun generate() {
        val project = taskDependencies.getDependencies(this).first().project // Needs compileJava
        val sourceSets = project.extensions.getByType<JavaPluginExtension>().sourceSets
        
        // Filter the classpath to only include elements that exist on disk
        val cp = sourceSets.getByName("main").runtimeClasspath
            .filter { it.exists() }
            .asPath

        val classesDir = project.layout.buildDirectory.dir("classes/java/main").get().asFile
        
        if (!classesDir.exists()) return

        val classes = classesDir.walk()
            .filter { it.isFile && it.extension == "class" && !it.name.contains("$") && it.name != "Main.class" }
            .map { it.absolutePath.substringAfter(classesDir.absolutePath + File.separator).replace(File.separator, ".").removeSuffix(".class") }
            .toList()

        classes.forEach { className ->
            println("--------------------------------------------------------")
            println("Generating tests for: $className")
            println("--------------------------------------------------------")
            execOperations.exec {
                commandLine(
                    "java", "-jar", "lib/evosuite/evosuite-1.0.6.jar",
                    "-class", className,
                    "-projectCP", cp,
                    "-Dsearch_budget=30",
                    "-Dstopping_condition=MaxTime",
                    "-Dshow_progress=false",
                    "-Dtest_dir=src/evosuite-tests"
                )
            }
        }
    }
}

tasks.register<EvoSuiteGenerateAllTask>("evosuiteGenerateAll") {
    group = "verification"
    dependsOn("compileJava")
}

tasks.withType<Test> {
    if (name != "test" && name != "testEvoSuite") {
        useJUnit()
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    jvmArgs = listOf("-Dfile.encoding=UTF-8")
}

pitest {
    junit5PluginVersion.set("1.2.1") 
    targetClasses.set(setOf("org.spotifumtp37.*")) 
    excludedTestClasses.set(setOf("*ESTest*")) 
    threads.set(4)
    outputFormats.set(setOf("HTML"))
    excludedClasses.set(setOf("org.spotifumtp37.delegate.*"))
    timestampedReports.set(false)
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}