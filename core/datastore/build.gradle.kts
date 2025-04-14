plugins {
    alias(libs.plugins.cashsense.android.library)
    alias(libs.plugins.cashsense.hilt)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "ru.resodostudios.cashsense.core.datastore"
}

dependencies {
    api(projects.core.datastoreProto)
    api(projects.core.model)
    api(libs.androidx.dataStore)

    implementation(projects.core.common)
}