plugins {
    alias(libs.plugins.cashsense.android.feature.api)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.transaction.editor.api"
}

dependencies {
    api(projects.core.navigation)
    api(projects.core.model)

    implementation(projects.feature.home.api)
    implementation(projects.feature.transaction.overview.api)
}
