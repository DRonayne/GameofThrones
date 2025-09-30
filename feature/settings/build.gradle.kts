plugins {
    id("gameofthrones.android.feature")
}

android {
    namespace = "com.darach.gameofthrones.feature.settings"
}

dependencies {
    implementation(project(":core:data"))
}
