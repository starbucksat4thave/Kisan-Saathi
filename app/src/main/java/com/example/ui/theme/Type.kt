package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val NotoNastaliqUrdu = FontFamily(
    Font(R.font.noto_nastaliq_urdu, FontWeight.Normal)
)

val NotoSansArabic = FontFamily(
    Font(R.font.noto_sans_arabic, FontWeight.Normal),
    Font(R.font.noto_sans_arabic, FontWeight.Medium),
    Font(R.font.noto_sans_arabic, FontWeight.Bold)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = NotoNastaliqUrdu,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 44.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = NotoNastaliqUrdu,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 38.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = NotoNastaliqUrdu,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 34.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSansArabic,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        lineHeight = 28.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = NotoSansArabic,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSansArabic,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = NotoSansArabic,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp,
    )
)
