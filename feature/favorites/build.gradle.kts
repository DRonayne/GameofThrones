plugins {
    id("gameofthrones.android.feature")
}

android {
    namespace = "com.darach.gameofthrones.feature.favorites"
}

dependencies {
    implementation(libs.androidx.compose.material3.adaptive)
}

kover {
    reports {
        verify {
            rule {
                minBound(17)
            }
        }
    }
}
