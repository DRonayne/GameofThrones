plugins {
    id("gameofthrones.android.feature")
}

android {
    namespace = "com.darach.gameofthrones.feature.characters"
}

dependencies {
    implementation(project(":core:network"))
}
