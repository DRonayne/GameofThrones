// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.kover) apply false
    id("dev.iurysouza.modulegraph") version "0.13.0"
}

moduleGraphConfig {
    readmePath.set("${rootDir}/README.md")
    heading.set("### Module Graph")
    setStyleByModuleType.set(true)
    theme.set(
        dev.iurysouza.modulegraph.Theme.BASE(
            mapOf(
                "primaryTextColor" to "#000000",
                "textColor" to "#000000",
                "mainBkg" to "#F8E287",
                "secondBkg" to "#EEE2BC",
                "primaryColor" to "#F4EDDF",
                "primaryBorderColor" to "#6D5E0F",
                "lineColor" to "#6D5E0F",
                "tertiaryColor" to "#FFF9EE",
                "fontSize" to "12px",
            ),
            focusColor = "#6D5E0F",
            moduleTypes = listOf(
                dev.iurysouza.modulegraph.ModuleType.AndroidApp("#F8E287"),
                dev.iurysouza.modulegraph.ModuleType.AndroidLibrary("#EEE2BC"),
            )
        )
    )
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktlint("1.7.1")
                .editorConfigOverride(
                    mapOf(
                        "android" to "true",
                        "ktlint_code_style" to "android_studio",
                        "ktlint_function_naming_ignore_when_annotated_with" to "Composable"
                    )
                )
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint("1.7.1")
        }
    }

    apply(plugin = "io.gitlab.arturbosch.detekt")
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
        baseline = file("$rootDir/config/detekt/baseline.xml")
    }

    dependencies {
        "detektPlugins"("io.nlopez.compose.rules:detekt:0.4.27")
    }
}
