import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("gameofthrones.android.library")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            val libs = extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java).named("libs")

            extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }

                packaging {
                    resources {
                        excludes += setOf(
                            "META-INF/LICENSE.md",
                            "META-INF/LICENSE-notice.md"
                        )
                    }
                }
            }

            dependencies {
                // Core modules - common dependencies for all features
                add("implementation", project(":core:model"))
                add("implementation", project(":core:common"))
                add("implementation", project(":core:analytics"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:ui"))

                // Compose
                val composeBom = libs.findLibrary("androidx.compose.bom").get()
                add("implementation", platform(composeBom))
                add("implementation", libs.findBundle("compose.ui").get())

                // Lifecycle
                add("implementation", libs.findLibrary("androidx.lifecycle.runtime.compose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())

                // Hilt Navigation
                add("implementation", libs.findLibrary("hilt.navigation.compose").get())

                // Testing
                add("testImplementation", libs.findLibrary("turbine").get())
                add("testImplementation", libs.findLibrary("konsist").get())

                add("androidTestImplementation", platform(composeBom))
                add("androidTestImplementation", libs.findBundle("compose.test").get())
                add("androidTestImplementation", libs.findLibrary("mockk-android").get())

                add("debugImplementation", libs.findBundle("compose.debug").get())
            }
        }
    }
}