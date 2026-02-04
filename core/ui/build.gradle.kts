plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.core.ui"
}

dependencies {
    api(projects.core.analytics)
    api(projects.core.designsystem)
    api(projects.core.locales)
    api(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation3.ui)
    api(libs.haze)
    api(libs.haze.materials)
    implementation(libs.lottie.compose)
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.core)
}
