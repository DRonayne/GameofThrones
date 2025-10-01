plugins {
    id("gameofthrones.android.library")
}

android {
    namespace = "com.darach.gameofthrones.core.analytics"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
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
