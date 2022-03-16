// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    extra.set("versionName", "0.0.1")
    extra.set("versionCode", 1)

    repositories {
        maven(url = "https://plugins.gradle.org/m2/")
        mavenCentral()
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.71")
        classpath("io.github.gradle-nexus:publish-plugin:1.1.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

apply(plugin = "io.github.gradle-nexus.publish-plugin")
apply(from = "${rootDir}/scripts/publish-root.gradle")
