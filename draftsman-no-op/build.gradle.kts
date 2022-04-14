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

ext {
    set("PUBLISH_GROUP_ID", "com.gojek.draftsman")
    set("PUBLISH_VERSION", "0.0.3")
    set("PUBLISH_ARTIFACT_ID", "draftsman-no-op")
}

apply(from = "${rootProject.projectDir}/scripts/publish-module.gradle")
