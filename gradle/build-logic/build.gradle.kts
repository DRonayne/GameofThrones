plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "gameofthrones.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "gameofthrones.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "gameofthrones.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}