package ru.resodostudios.cashsense

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

@Suppress("EnumEntryName")
enum class CsFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    demo(FlavorDimension.contentType, applicationIdSuffix = ".demo"),
    prod(FlavorDimension.contentType),
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: CsFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.entries.forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            CsFlavor.entries.forEach { csFlavor ->
                register(csFlavor.name) {
                    dimension = csFlavor.dimension.name
                    flavorConfigurationBlock(this, csFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (csFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = csFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
