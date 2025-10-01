plugins {
    id("gameofthrones.android.library")
}

android {
    namespace = "com.darach.gameofthrones.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.coroutines)

    // Firebase (for monitoring)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)

    testImplementation(libs.turbine)
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
