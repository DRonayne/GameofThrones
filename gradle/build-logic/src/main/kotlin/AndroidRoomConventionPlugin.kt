import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
            }

            val libs = extensions.getByType<org.gradle.api.artifacts.VersionCatalogsExtension>().named("libs")
            dependencies {
                add("ksp", libs.findLibrary("room.compiler").get())
            }
        }
    }
}