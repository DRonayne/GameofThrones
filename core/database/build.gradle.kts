plugins {
    id("gameofthrones.android.library")
}

android {
    namespace = "com.darach.gameofthrones.core.database"

    defaultConfig {
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

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
    implementation(project(":core:common"))

    // Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Android Testing - Room specific
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.room.testing)
}

kover {
    reports {
        verify {
            rule {
                minBound(4)
            }
        }
    }
}
