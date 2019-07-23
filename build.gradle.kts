plugins {
    `java-library`
    `maven-publish`
    signing
}

apply(from = "jdks.gradle.kts")

repositories {
    jcenter()
}

sourceSets {
    main {
        java {
            exclude("module-info.java")
        }
    }
    create("moduleInfo") {
        java {
            // We need the entire source directory here, otherwise we get a
            // "package is empty or does not exist" error during compilation.
            srcDir("src/main/java")
            compileClasspath = sourceSets.main.get().compileClasspath
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.12")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

tasks.named<JavaCompile>("compileModuleInfoJava") {
    sourceCompatibility = "9"
    targetCompatibility = "9"

    doLast {
        // Leave only the module-info.class
        delete("$destinationDir/cafe")
    }
}

tasks.jar {
    // Add the Java 9+ module-info.class to the Java 7+ classes
    from(sourceSets["moduleInfo"].output)
}

group = "cafe.cryptography"
version = "0.1.0-SNAPSHOT"

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("spake2")
                description.set("Pure Java implementation of the SPAKE2 protocol")
                url.set("https://cryptography.cafe")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("str4d")
                        name.set("Jack Grigg")
                        email.set("thestr4d@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/cryptography-cafe/spake2.git")
                    developerConnection.set("scm:git:ssh://github.com:cryptography-cafe/spake2.git")
                    url.set("https://github.com/cryptography-cafe/spake2/tree/master")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotRepoUrl else releasesRepoUrl)
            credentials {
                val NEXUS_USERNAME: String? by project
                val NEXUS_PASSWORD: String? by project
                username = NEXUS_USERNAME ?: ""
                password = NEXUS_PASSWORD ?: ""
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

apply(from = "javadoc.gradle.kts")
