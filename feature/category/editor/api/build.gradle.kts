plugins {
    alias(libs.plugins.cashsense.android.feature.api)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.category.editor.api"
}

dependencies {
    api(projects.core.navigation)
}
