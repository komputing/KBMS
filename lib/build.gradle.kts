plugins {
    kotlin("jvm") version "1.9.0"
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("com.google.truth:truth:1.1.4")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.komputing.kbms"
            artifactId = "library"
            version = "0.2"

            from(components["java"])
        }
    }
}