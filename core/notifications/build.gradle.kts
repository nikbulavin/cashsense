plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.hilt)
}

android {
    namespace = "ru.resodostudios.cashsense.core.notifications"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.locales)
}
