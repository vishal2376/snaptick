package com.vishal2376.snaptick.presentation.settings.common

enum class TopLanguage(
	val languageCode: String,
	val endonym: String,
	val emoji: String
) {
	CHINESE("zh", "中文", "\uD83C\uDDE8\uD83C\uDDF3"),
	DANISH("da", "Dansk", "\uD83C\uDDE9\uD83C\uDDF0"),
	DUTCH("nl", "Nederlands","\uD83C\uDDF3\uD83C\uDDF1"),
	ENGLISH("en", "English", "\uD83C\uDDEC\uD83C\uDDE7"),
	FRENCH("fr", "Français", "\uD83C\uDDEB\uD83C\uDDF7"),
	GERMAN("de", "Deutsch", "\uD83C\uDDE9\uD83C\uDDEA"),
	ITALIAN("it", "Italiano", "\uD83C\uDDEE\uD83C\uDDF9"),
	JAPANESE("ja", "日本語","\uD83C\uDDEF\uD83C\uDDF5"),
	NORWEGIAN("no", "Norsk bokmål", "\uD83C\uDDF3\uD83C\uDDF4"),
	POLISH("pl", "Polski", "\uD83C\uDDF5\uD83C\uDDF1"),
	PERSIAN("fa", "فارسی\u200E", "\uD83C\uDDE7\uD83C\uDDEB"),
	PORTUGUESE("pt", "Português", "\uD83C\uDDF5\uD83C\uDDF9"),
	RUSSIAN("ru", "Русский", "\uD83C\uDDF7\uD83C\uDDFA"),
	SPANISH("es", "Español", "\uD83C\uDDEA\uD83C\uDDF8"),
	TURKISH("tr", "Türkçe", "\uD83C\uDDF9\uD83C\uDDF7"),
	UKRAINIAN("uk", "Українська", "\uD83C\uDDFA\uD83C\uDDE6"),
	VIETNAMESE("vi", "Tiếng Việt", "\uD83C\uDDFB\uD83C\uDDF3");
}