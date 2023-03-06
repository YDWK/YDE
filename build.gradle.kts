import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import java.net.URL
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories { mavenCentral() }

    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.7.20")
        classpath("io.codearte.gradle.nexus:gradle-nexus-staging-plugin:0.30.0")
    }
}

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("com.diffplug.spotless")
    id("org.jetbrains.dokka")
    id("io.gitlab.arturbosch.detekt")
    application
    `maven-publish`
    signing
    jacoco // code coverage reports
}

apply(plugin = "io.codearte.nexus-staging")

extra.apply {
    set("name", "YDE")
    set(
        "description",
        "YDE (Yusuf's Discord Entity) contains all the entities that are used in YDWK")
    set("dev_id", "yusuf")
    set("dev_name", "Yusuf Ismail")
    set("dev_email", "yusufgamer222@gmail.com")
    set("dev_organization", "YDWK")
    set("dev_organization_url", "https://github.com/YDWK")
    set("gpl_name", "Apache-2.0 license")
    set("gpl_url", "https://github.com/YDWK/YDE/blob/master/LICENSE")
}

group = "io.github.realyusufismail" // used for publishing. DON'T CHANGE

val releaseVersion by extra(!version.toString().endsWith("-SNAPSHOT"))

repositories { mavenCentral() }

dependencies {

    // json
    api(
        "com.fasterxml.jackson.module:jackson-module-kotlin:" +
            properties["jacksonModuleKotlinVersion"])

    // logger
    api("ch.qos.logback:logback-classic:" + properties["logBackClassicVersion"])
    api("ch.qos.logback:logback-core:" + properties["logBackCoreVersion"])
    api("uk.org.lidalia:sysout-over-slf4j:" + properties["sysoutOverSlf4jVersion"])

    // https
    api("com.squareup.okhttp3:okhttp:" + properties["okhttp3Version"])

    testImplementation(kotlin("test"))
}

apply(from = "gradle/tasks/Nexus.gradle")

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "11" }

tasks.build {
    dependsOn(tasks.test) // run tests before building

    if (releaseVersion) {
        // check if MAVEN_PASSWORD is set
        if (System.getenv("MAVEN_PASSWORD") != null) {
            // run publishYdwkPublicationToMavenCentralRepository
            dependsOn(tasks.getByName("publishYdwkPublicationToMavenCentralRepository"))
            // then increment version
            dependsOn(tasks.getByName("incrementVersion"))
        } else {
            // ignore
        }
    }
}

tasks.jacocoTestReport {
    group = "Reporting"
    description = "Generate Jacoco coverage reports after running tests."
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    finalizedBy("jacocoTestCoverageVerification")
}

configurations { all { exclude(group = "org.slf4j", module = "slf4j-log4j12") } }

spotless {
    kotlin {
        // Excludes build folder since it contains generated java classes.
        targetExclude("build/**")
        ktfmt("0.42").dropboxStyle()

        licenseHeader(
            """/*
 * Copyright 2022 YDWK inc.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ """)
    }

    kotlinGradle {
        target("**/*.gradle.kts")
        ktfmt("0.42").dropboxStyle()
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}

detekt {
    source = files("src/main/kotlin/io/github/ydwk/yde/entities")
    config = files("gradle/config/detekt.yml")
    baseline = file("gradle/config/detekt-baseline.xml")
    allRules = false
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

java {
    withJavadocJar()
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Detekt>().configureEach { jvmTarget = "11" }

tasks.withType<DetektCreateBaselineTask>().configureEach { jvmTarget = "11" }

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to project.extra["dev_organization"],
            "Implementation-Vendor-Id" to project.extra["dev_id"],
            "Implementation-Vendor-Name" to project.extra["dev_name"],
            "Implementation-Vendor-Email" to project.extra["dev_email"],
            "Implementation-Vendor-Organization" to project.extra["dev_organization"],
            "Implementation-Vendor-Organization-Url" to project.extra["dev_organization_url"],
            "Implementation-License" to project.extra["gpl_name"],
            "Implementation-License-Url" to project.extra["gpl_url"],
        )
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

publishing {
    val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")
    publications {
        create<MavenPublication>("yde") {
            from(components["java"])
            // artifactId = project.artifactId // or maybe archiveBaseName?
            pom {
                name.set(extra["name"] as String)
                description.set(extra["description"] as String)
                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/YDWK/YDE/issues")
                }
                licenses {
                    license {
                        name.set(extra["gpl_name"] as String)
                        url.set(extra["gpl_url"] as String)
                    }
                }
                ciManagement { system.set("GitHub Actions") }
                inceptionYear.set("2023")
                developers {
                    developer {
                        id.set(extra["dev_id"] as String)
                        name.set(extra["dev_name"] as String)
                        email.set(extra["dev_email"] as String)
                        organization.set(extra["dev_organization"] as String)
                        organizationUrl.set(extra["dev_organization_url"] as String)
                    }
                }
                scm {
                    connection.set("https://github.com/YDWK/YDE.git")
                    developerConnection.set("scm:git:ssh://git@github.com/YDWK/YDE.git")
                    url.set("github.com/YDWK/YDE")
                }
            }
        }
    }
    repositories {
        maven {
            name = "MavenCentral"
            val releaseRepo = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotRepo = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri((if (isReleaseVersion) releaseRepo else snapshotRepo))
            credentials {
                // try to get it from system gradle.properties
                logger.debug("Trying to get credentials from system gradle.properties")
                username =
                    when {
                        systemHasEnvVar("MAVEN_USERNAME") -> {
                            logger.debug("Found username in system gradle.properties")
                            System.getenv("MAVEN_USERNAME")
                        }
                        project.hasProperty("MAVEN_USERNAME") -> {
                            logger.debug("MAVEN_USERNAME found in gradle.properties")
                            project.property("MAVEN_USERNAME") as String
                        }
                        else -> {
                            logger.debug(
                                "MAVEN_USERNAME not found in system properties, meaning if you are trying to publish to maven central, it will fail")
                            null
                        }
                    }

                password =
                    when {
                        systemHasEnvVar("MAVEN_PASSWORD") -> {
                            logger.debug("Found password in system gradle.properties")
                            System.getenv("MAVEN_PASSWORD")
                        }
                        project.hasProperty("MAVEN_PASSWORD") -> {
                            logger.debug("MAVEN_PASSWORD found in gradle.properties")
                            project.property("MAVEN_PASSWORD") as String
                        }
                        else -> {
                            logger.debug(
                                "MAVEN_PASSWORD not found in system properties, meaning if you are trying to publish to maven central, it will fail")
                            null
                        }
                    }
            }
        }
    }
}

fun systemHasEnvVar(varName: String): Boolean {
    return System.getenv(varName) != null
}

signing {
    afterEvaluate {
        // println "sign: " + isReleaseVersion
        val isRequired =
            releaseVersion &&
                (tasks.withType<PublishToMavenRepository>().find { gradle.taskGraph.hasTask(it) } !=
                    null)
        setRequired(isRequired)
        sign(publishing.publications["yde"])
    }
}

tasks.getByName("dokkaHtml", DokkaTask::class) {
    dokkaSourceSets.configureEach {
        includes.from("Package.md")
        jdkVersion.set(11)
        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl.set(URL("https://github.com/YDWWk/YDE/tree/master/src/main/kotlin"))
            remoteLineSuffix.set("#L")
        }

        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage = "Copyright © 2023 YDWK inc."
        }
    }
}
