import ru.resodostudios.cashsense.CsBuildType

plugins {
    alias(libs.plugins.cashsense.android.application)
    alias(libs.plugins.cashsense.android.application.compose)
    alias(libs.plugins.cashsense.android.application.firebase)
    alias(libs.plugins.cashsense.android.application.flavors)
    alias(libs.plugins.cashsense.hilt)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.kotlin.serialization)
}

android {
    defaultConfig {
        applicationId = "ru.resodostudios.cashsense"
        versionCode = 53
        versionName = "2.0.0-beta01"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = CsBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            applicationIdSuffix = CsBuildType.RELEASE.applicationIdSuffix
            baselineProfile.automaticGenerationDuringBuild = true
            signingConfig = signingConfigs.named("debug").get()
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    namespace = "ru.resodostudios.cashsense"
}

baselineProfile {
    automaticGenerationDuringBuild = false
    dexLayoutOptimization = true
}

dependencies {
    implementation(projects.feature.category.dialog)
    implementation(projects.feature.category.list)
    implementation(projects.feature.home)
    implementation(projects.feature.settings)
    implementation(projects.feature.subscription.dialog)
    implementation(projects.feature.subscription.list)
    implementation(projects.feature.transaction.dialog)
    implementation(projects.feature.transaction.overview)
    implementation(projects.feature.transfer)
    implementation(projects.feature.wallet.detail)
    implementation(projects.feature.wallet.dialog)
    implementation(projects.feature.wallet.widget)

    implementation(projects.core.data)
    implementation(projects.core.designsystem)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.ui)

    implementation(projects.work)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing)
    implementation(libs.kotlinx.serialization.json)

    debugImplementation(libs.androidx.compose.ui.testManifest)
    debugImplementation(libs.leakcanary.android)

    baselineProfile(projects.baselineprofile)
}