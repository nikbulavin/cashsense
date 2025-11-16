plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.android.room)
    alias(libs.plugins.cashsense.hilt)
}

android {
    namespace = "ru.resodostudios.cashsense.core.database"
}

dependencies {
    api(projects.core.model)

    implementation(projects.core.common)

    implementation(libs.kotlinx.datetime)
    implementation(libs.zip4j)
}
