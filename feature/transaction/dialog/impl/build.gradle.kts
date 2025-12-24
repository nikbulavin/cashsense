plugins {
    alias(libs.plugins.cashsense.android.feature.impl)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.transaction.dialog.impl"
}

dependencies {
    implementation(projects.core.data)

    implementation(projects.feature.transaction.dialog.api)

    implementation(libs.androidx.navigation3.ui)
}