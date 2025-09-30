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
            }

            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:ui"))

                add("implementation", libs.findLibrary("androidx.lifecycle.runtime.compose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())

                val composeBom = libs.findLibrary("androidx.compose.bom").get()
                add("implementation", platform(composeBom))
                add("implementation", libs.findLibrary("androidx.compose.ui").get())
                add("implementation", libs.findLibrary("androidx.compose.ui.graphics").get())
                add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
                add("implementation", libs.findLibrary("androidx.compose.material3").get())
                add("implementation", libs.findLibrary("androidx.compose.material-icons-extended").get())

                add("implementation", libs.findLibrary("hilt.navigation.compose").get())

                add("testImplementation", libs.findLibrary("turbine").get())
                add("testImplementation", libs.findLibrary("konsist").get())

                add("androidTestImplementation", libs.findLibrary("androidx.junit").get())
                add("androidTestImplementation", platform(composeBom))
                add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test.junit4").get())

                add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
                add("debugImplementation", libs.findLibrary("androidx.compose.ui.test.manifest").get())
            }
        }
    }
}