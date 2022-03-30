plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
    }

    sourceSets["main"].java.srcDirs(
        "src/main/kotlin"
    )
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.31")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
}

ext {
    set("PUBLISH_GROUP_ID", "com.gojek.draftsman")
    set("PUBLISH_VERSION", "0.0.3")
    set("PUBLISH_ARTIFACT_ID", "draftsman")
}

apply(from = "${rootProject.projectDir}/scripts/publish-module.gradle")
