plugins {
    id("gameofthrones.android.library")
}

android {
    namespace = "com.darach.gameofthrones.core.domain"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    testImplementation(libs.turbine)
}

kover {
    reports {
        verify {
            rule {
                minBound(71)
            }
        }
    }
}
