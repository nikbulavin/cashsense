plugins {
    alias(libs.plugins.cashsense.android.feature.impl)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.transaction.importer.impl"
}

dependencies {
    implementation(projects.feature.transaction.importer.api)

    implementation(projects.core.domain)

    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlin.csv)
}
