package ru.resodostudios.cashsense.core.model.data

enum class Language(
    val code: String,
    val displayName: String,
) {
    ENGLISH("en", "English"),
    RUSSIAN("ru", "Русский"),
    ARABIC("ar", "العربية"),
    GERMAN("de", "Deutsch"),
    SPANISH("es", "Español"),
    FRENCH("fr", "Français"),
    HINDI("hi", "हिंदी"),
    ITALIAN("it", "Italiano"),
    JAPANESE("ja", "日本語"),
    KOREAN("ko", "한국어"),
    POLISH("pl", "Polski"),
    TAMIL("ta", "தமிழ்"),
    CHINESE_SIMPLIFIED("zh", "简体中文"),
}