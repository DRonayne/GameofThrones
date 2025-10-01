plugins {
    id("gameofthrones.android.feature")
}

android {
    namespace = "com.darach.gameofthrones.feature.comparison"
}

kover {
    reports {
        verify {
            rule {
                minBound(15)
            }
        }
    }
}
