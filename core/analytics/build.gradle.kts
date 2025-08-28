plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.android.library.compose)
    alias(libs.plugins.cashsense.hilt)
}

android {
    namespace = "ru.resodostudios.cashsense.core.analytics"
}

dependencies {
    implementation(libs.androidx.compose.runtime)

    prodImplementation(platform(libs.firebase.bom))
    prodImplementation(libs.firebase.analytics)
}
