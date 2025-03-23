package ru.resodostudios.cashsense

import ru.resodostudios.cashsense.baselineprofile.BuildConfig

val PACKAGE_NAME = buildString {
    append("ru.resodostudios.cashsense")
    append(BuildConfig.APP_FLAVOR_SUFFIX)
}