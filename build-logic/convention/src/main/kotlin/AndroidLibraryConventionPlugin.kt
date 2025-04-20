import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import ru.resodostudios.cashsense.configureFlavors
import ru.resodostudios.cashsense.configureKotlinAndroid
import ru.resodostudios.cashsense.disableUnnecessaryAndroidTests
import ru.resodostudios.cashsense.libs

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
                configureFlavors(this)
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                disableUnnecessaryAndroidTests(target)
            }
            dependencies {
                "testImplementation"(kotlin("test"))

                "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
            }
        }
    }
}
