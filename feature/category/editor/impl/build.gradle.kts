plugins {
    alias(libs.plugins.cashsense.android.feature.impl)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.category.editor.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.locales)

    implementation(projects.feature.category.editor.api)

    implementation(libs.androidx.compose.material3.adaptive.navigation3)
}
