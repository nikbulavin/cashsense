plugins {
    alias(libs.plugins.cashsense.android.feature.impl)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.subscription.list.impl"
}

dependencies {
    implementation(projects.core.data)

    implementation(projects.feature.subscription.dialog.api)
    implementation(projects.feature.subscription.list.api)
}