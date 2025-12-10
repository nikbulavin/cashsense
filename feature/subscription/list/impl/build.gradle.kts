plugins {
    alias(libs.plugins.cashsense.android.feature.impl)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.subscription.list.impl"
}

dependencies {
    implementation(projects.core.data)
}