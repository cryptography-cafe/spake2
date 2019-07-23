plugins {
    `java-library`
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
