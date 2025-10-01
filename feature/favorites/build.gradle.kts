plugins {
    id("gameofthrones.android.feature")
}

android {
    namespace = "com.darach.gameofthrones.feature.favorites"
}

kover {
    reports {
        verify {
            rule {
                minBound(18)
            }
        }
    }
}
