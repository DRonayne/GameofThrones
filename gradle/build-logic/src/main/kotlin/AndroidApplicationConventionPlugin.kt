import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
                apply("com.google.devtools.ksp")
                apply("com.google.dagger.hilt.android")
                apply("org.jetbrains.kotlinx.kover")
            }

            val libs = extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java).named("libs")

            extensions.configure<ApplicationExtension> {
                compileSdk = libs.findVersion("compileSdk").get().toString().toInt()

                defaultConfig {
                    minSdk = libs.findVersion("minSdk").get().toString().toInt()
                    targetSdk = libs.findVersion("targetSdk").get().toString().toInt()

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables {
                        useSupportLibrary = true
                    }
                }

                buildFeatures {
                    compose = true
                    buildConfig = true
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                }
            }

            dependencies {
                // AndroidX Core
                add("implementation", libs.findBundle("androidx.core").get())

                // Compose
                add("implementation", libs.findLibrary("androidx.activity.compose").get())
                val composeBom = libs.findLibrary("androidx.compose.bom").get()
                add("implementation", platform(composeBom))
                add("implementation", libs.findBundle("compose.ui").get())

                // Navigation
                add("implementation", libs.findLibrary("androidx.navigation.compose").get())
                add("implementation", libs.findLibrary("kotlinx.serialization.json").get())

                // Hilt
                add("implementation", libs.findLibrary("hilt.android").get())
                add("ksp", libs.findLibrary("hilt.android.compiler").get())
                add("implementation", libs.findLibrary("hilt.navigation.compose").get())

                // Testing
                add("testImplementation", libs.findBundle("testing.unit").get())
                add("testImplementation", libs.findLibrary("turbine").get())
                add("testImplementation", libs.findLibrary("konsist").get())

                add("androidTestImplementation", libs.findBundle("testing.android").get())
                add("androidTestImplementation", platform(composeBom))
                add("androidTestImplementation", libs.findBundle("compose.test").get())
                add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test.manifest").get())
                add("androidTestImplementation", libs.findLibrary("truth").get())

                add("debugImplementation", libs.findBundle("compose.debug").get())
            }
        }
    }
}