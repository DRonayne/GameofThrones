plugins {
    id("gameofthrones.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.darach.gameofthrones.core.network"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
}
