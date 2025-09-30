plugins {
    id("gameofthrones.android.library")
}

android {
    namespace = "com.darach.gameofthrones.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain"))

    implementation(libs.datastore.preferences)
    implementation(libs.datastore.preferences.core)

    testImplementation(libs.turbine)
}
