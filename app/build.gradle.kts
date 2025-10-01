plugins {
    id("gameofthrones.android.application")
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.darach.gameofthrones"

    defaultConfig {
        applicationId = "com.darach.gameofthrones"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = true
            }
        }
    }
}

dependencies {
    // Feature modules
    implementation(project(":feature:characters"))
    implementation(project(":feature:character-detail"))
    implementation(project(":feature:favorites"))
    implementation(project(":feature:comparison"))
    implementation(project(":feature:settings"))

    // Core modules
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    // Adaptive & Window
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.window)
    implementation(libs.androidx.window.core)

    // WorkManager
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // LeakCanary
    debugImplementation(libs.leakcanary)

    // Baseline Profile
    implementation(libs.androidx.profileinstaller)
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*_Factory",
                    "*_HiltModules*",
                    "*Hilt_*",
                    "*.BuildConfig",
                    "*.ComposableSingletons*",
                    "*_Impl*",
                    "dagger.hilt.*",
                )
                packages(
                    "hilt_aggregated_deps",
                    "dagger.hilt.internal",
                )
            }
        }
        verify {
            rule {
                minBound(0)
            }
        }
    }
}
