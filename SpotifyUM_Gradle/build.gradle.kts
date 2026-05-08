import org.gradle.process.ExecOperations
import javax.inject.Inject

plugins {
    id("java")
    id("application")
    id("jacoco")
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
    testImplementation(files("lib/evosuite/evosuite-standalone-runtime-1.0.6.jar"))
    testImplementation("junit:junit:4.12")
}

sourceSets {
    val test by getting {
        java.srcDir("src/evosuite-tests")
        java.srcDir("src/hypothesis-tests")
        resources.srcDir("src/hypothesis-tests")
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
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test, tasks.named("testEvoSuite")) // tests are required to run before generating the report
    
    executionData.setFrom(fileTree(layout.buildDirectory).include("/jacoco/*.exec"))
    
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

// EvoSuite tests often use JUnit 4
tasks.withType<Test> {
    if (name != "test" && name != "testEvoSuite") {
        useJUnit()
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
    jvmArgs = listOf("-Dfile.encoding=UTF-8")
}