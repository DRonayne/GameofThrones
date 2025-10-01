import java.util.Properties

plugins {
    id("gameofthrones.android.library")
    alias(libs.plugins.kotlin.serialization)
}

// Read local.properties
val localProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            load(localPropertiesFile.inputStream())
        }
    }

android {
    namespace = "com.darach.gameofthrones.core.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField(
            "String",
            "GOT_BASE_URL",
            "\"${localProperties.getProperty("GOT_BASE_URL", "")}\"",
        )
        buildConfigField(
            "String",
            "GOT_API_TOKEN",
            "\"${localProperties.getProperty("GOT_API_TOKEN", "")}\"",
        )
    }
}

dependencies {
    implementation(project(":core:common"))

    // Networking
    implementation(libs.bundles.networking)

    // Testing
    testImplementation(libs.okhttp.mockwebserver)
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
