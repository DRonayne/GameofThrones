plugins {
    id("gameofthrones.android.library")
}

android {
    namespace = "com.darach.gameofthrones.core.model"
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
