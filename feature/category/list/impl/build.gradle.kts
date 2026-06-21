plugins {
    alias(libs.plugins.cashsense.android.feature.impl)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.category.list.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.locales)

    implementation(projects.feature.category.detail.api)
    implementation(projects.feature.category.editor.api)
    implementation(projects.feature.category.list.api)
    implementation(projects.feature.subscription.dialog.api)
    implementation(projects.feature.wallet.dialog.api)

    implementation(libs.androidx.compose.material3.adaptive.navigation3)
}