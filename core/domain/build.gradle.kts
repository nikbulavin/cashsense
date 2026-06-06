plugins {
    alias(libs.plugins.cashsense.android.library)
}

android {
    namespace = "ru.resodostudios.cashsense.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.javax.inject)
    implementation(libs.kotlin.csv)
}
