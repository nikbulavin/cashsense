plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.category.list.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.locales)
}