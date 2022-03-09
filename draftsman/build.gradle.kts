plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String
    }

    sourceSets["main"].java.srcDirs(
        "src/main/kotlin"
    )
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.71")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.toString())
}

publishing {
    publications {
        create<MavenPublication>("draftsman") {
            groupId = "com.gojek.draftsman"
            artifactId = "draftsman"
            version = rootProject.extra["versionName"] as String
            // Tell maven to prepare the generated "*.aar" file for publishing
            artifact(sourcesJar)
            artifact("$buildDir/outputs/aar/${project.name}-release.aar")
            pom {
                withXml {
                    asNode().appendNode("dependencies").let {
                        for (dependency in configurations["implementation"].dependencies) {
                            it.appendNode("dependency").apply {
                                appendNode("groupId", dependency.group)
                                appendNode("artifactId", dependency.name)
                                appendNode("version", dependency.version)
                            }
                        }
                    }
                }
            }
        }
    }
}
