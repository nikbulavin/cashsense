plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewModelNavigation3)
}