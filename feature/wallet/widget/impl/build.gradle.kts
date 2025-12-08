plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.wallet.widget.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.domain)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
}
