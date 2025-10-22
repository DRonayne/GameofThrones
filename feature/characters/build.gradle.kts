plugins {
    id("gameofthrones.android.feature")
}

android {
    namespace = "com.darach.gameofthrones.feature.characters"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":feature:character-detail"))

    // Material 3 Adaptive for ListDetailPaneScaffold
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
}

kover {
    reports {
        verify {
            rule {
                minBound(8)
            }
        }
    }
}
