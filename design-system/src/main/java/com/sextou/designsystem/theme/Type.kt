package com.sextou.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val SextouBodyFont = FontFamily.SansSerif
private val SextouDisplayFont = FontFamily.SansSerif
private val SextouCompactActionText = TextStyle(
    fontFamily = SextouBodyFont,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = (-0.15).sp,
)

object SextouTextStyles {
    val Brand = TextStyle(
        fontFamily = SextouDisplayFont,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.75).sp,
    )

    val BrandSubtitle = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 9.sp,
        lineHeight = 13.5.sp,
        letterSpacing = 1.067.sp,
    )

    val SectionTitle = TextStyle(
        fontFamily = SextouDisplayFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )

    val CardTitle = TextStyle(
        fontFamily = SextouDisplayFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
    )

    val Search = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )

    val Category = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.6.sp,
    )

    val Metadata = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    )

    val NavigationLabel = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    )

    val Status = SextouCompactActionText
    val ActionButton = SextouCompactActionText
}

val SextouTypography = Typography(
    displayLarge = SextouTextStyles.Brand,
    headlineLarge = SextouTextStyles.SectionTitle,
    headlineMedium = SextouTextStyles.CardTitle,
    titleLarge = SextouTextStyles.CardTitle,
    titleMedium = SextouTextStyles.SectionTitle,
    bodyLarge = SextouTextStyles.Search,
    bodyMedium = SextouTextStyles.Metadata,
    labelLarge = SextouTextStyles.NavigationLabel,
    labelMedium = SextouTextStyles.Metadata,
    labelSmall = SextouTextStyles.Category,
)
