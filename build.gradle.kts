group = "me.xemor"
version = "1.5.1"
description = "foliahacks"
java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
    java
    `maven-publish`
    id("io.sentry.jvm.gradle") version("3.12.0")
}

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://mvn-repo.arim.space/lesser-gpl3")}
    mavenLocal()
}

dependencies {
    compileOnly("space.arim.morepaperlib:morepaperlib:0.4.3")
    compileOnly("org.spigotmc:spigot-api:1.20.5-R0.1-SNAPSHOT")
}

publishing {
    repositories {
        maven {
            name = "xemorReleases"
            url = uri("https://repo.xemor.zip/releases")
            credentials(PasswordCredentials::class)
            authentication {
                isAllowInsecureProtocol = true
                create<BasicAuthentication>("basic")
            }
        }

        maven {
            name = "xemorSnapshots"
            url = uri("https://repo.xemor.zip/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                isAllowInsecureProtocol = true
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()
            from(project.components["java"])
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
//End of auto generated

// Handles version variables etc
tasks.processResources {
    inputs.property("version", rootProject.version)
    expand("version" to rootProject.version)
}

publishing {

}
