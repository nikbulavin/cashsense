import com.android.build.api.dsl.ManagedVirtualDevice
import ru.resodostudios.cashsense.configureFlavors

plugins {
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.cashsense.android.test)
}

android {
    namespace = "ru.resodostudios.cashsense.baselineprofile"

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "APP_BUILD_TYPE_SUFFIX", "\"\"")
    }

    buildFeatures {
        buildConfig = true
    }

    configureFlavors(this) { flavor ->
        buildConfigField(
            "String",
            "APP_FLAVOR_SUFFIX",
            "\"${flavor.applicationIdSuffix ?: ""}\""
        )
    }

    testOptions.managedDevices.allDevices {
        create<ManagedVirtualDevice>("pixel6Api35") {
            device = "Pixel 6"
            apiLevel = 35
            systemImageSource = "aosp"
        }
    }

    targetProjectPath = ":mobile"
}

baselineProfile {
    managedDevices.clear()
    managedDevices += "pixel6Api35"
    useConnectedDevices = false
}

dependencies {
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.test.espresso.core)
    implementation(libs.androidx.test.ext)
    implementation(libs.androidx.test.rules)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.uiautomator)
}