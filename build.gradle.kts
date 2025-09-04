/*
 *    Copyright 2024 Canary Prism <canaryprsn@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.canary-prism"
description = "Minesweeper backend thing for Java"
version = "1.1.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

mavenPublishing {
    publishToMavenCentral(true)

    signAllPublications()

    pom {

        name = project.name
        description = project.description

        url = "https://github.com/Canary-Prism/minsweeper-java"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "Canary-Prism"
                name = "Canary Prism"
                email = "canaryprsn@gmail.com"
            }
        }

        scm {
            url = "https://github.com/Canary-Prism/minsweeper-java"
            connection = "scm:git:git://github.com/Canary-Prism/minsweeper-java.git"
            developerConnection = "scm:git:ssh://git@github.com:Canary-Prism/minsweeper-java.git"
        }
    }
}

tasks.javadoc {
    javadocTool = javaToolchains.javadocToolFor {
        languageVersion = JavaLanguageVersion.of(23)
    }

    (options as StandardJavadocDocletOptions).tags(
        "apiNote:a:API Note:",
        "implSpec:a:Implementation Requirements:",
        "implNote:a:Implementation Note:"
    )
}


tasks.withType<PublishToMavenRepository>().configureEach {
    notCompatibleWithConfigurationCache("Publishing tasks involve non-cacheable external interactions.")
}