package com.example.util

import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String
) {
    URDU("ur", "Urdu", "اردو"),
    ENGLISH("en", "English", "English")
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.URDU }

val AppLanguage.isUrdu: Boolean
    get() = this == AppLanguage.URDU
