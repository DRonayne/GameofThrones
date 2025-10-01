plugins {
    id("gameofthrones.android.library")
}

android {
    namespace = "com.darach.gameofthrones.core.data"

    packaging {
        resources {
            excludes +=
                setOf(
                    "META-INF/LICENSE.md",
                    "META-INF/LICENSE-notice.md",
                )
        }
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    // DataStore
    implementation(libs.datastore.preferences)
    implementation(libs.datastore.preferences.core)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Testing
    testImplementation(libs.turbine)

    // Android Testing - Integration test specific
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.okhttp.mockwebserver)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.retrofit)
    androidTestImplementation(libs.retrofit.kotlinx.serialization)
    androidTestImplementation(libs.kotlinx.serialization.json)
    androidTestImplementation(libs.mockk.android)
    kspAndroidTest(libs.hilt.android.compiler)
}

kover {
    reports {
        verify {
            rule {
                minBound(59)
            }
        }
    }
}
