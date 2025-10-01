plugins {
    id("gameofthrones.android.library")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.darach.gameofthrones.core.ui"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))

    // AndroidX Core
    implementation(libs.bundles.androidx.core)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)

    // Window
    implementation(libs.androidx.window)
    implementation(libs.androidx.window.core)

    // Coil
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Hilt
    implementation(libs.hilt.navigation.compose)

    // Testing
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.bundles.compose.test)

    debugImplementation(libs.bundles.compose.debug)
}

kover {
    reports {
        verify {
            rule {
                minBound(0)
            }
        }
    }
}
