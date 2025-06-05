plugins {
    alias(libs.plugins.cashsense.android.feature)
    alias(libs.plugins.cashsense.android.library.compose)
    alias(libs.plugins.aboutlibraries)
}

android {
    namespace = "ru.resodostudios.cashsense.feature.settings"
}

aboutLibraries {
    export {
        prettyPrint = true
    }

    library {
        duplicationMode = com.mikepenz.aboutlibraries.plugin.DuplicateMode.MERGE
        duplicationRule = com.mikepenz.aboutlibraries.plugin.DuplicateRule.SIMPLE
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.locales)

    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose)
    implementation(libs.androidx.browser)
}