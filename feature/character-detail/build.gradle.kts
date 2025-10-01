plugins {
    id("gameofthrones.android.feature")
}

android {
    namespace = "com.darach.gameofthrones.feature.characterdetail"
}

kover {
    reports {
        verify {
            rule {
                minBound(13)
            }
        }
    }
}
