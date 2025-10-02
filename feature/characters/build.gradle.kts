plugins {
    id("gameofthrones.android.feature")
}

android {
    namespace = "com.darach.gameofthrones.feature.characters"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:network"))
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
