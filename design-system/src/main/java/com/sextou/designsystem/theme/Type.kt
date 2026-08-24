package com.sextou.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

private val SextouBodyFont = FontFamily.SansSerif
private val SextouDisplayFont = FontFamily.SansSerif
private val SextouCompactActionText = TextStyle(
    fontFamily = SextouBodyFont,
    fontWeight = SextouPrimitiveFontWeights.Bold,
    fontSize = SextouPrimitiveTypography.Sm,
    lineHeight = SextouPrimitiveTypography.SmLineHeight,
    letterSpacing = (-0.15).sp,
)

object SextouTextStyles {
    // Semantic typography roles from the Figma sheet (node 19:785).
    val DisplayLarge = TextStyle(
        fontFamily = SextouDisplayFont,
        fontWeight = SextouPrimitiveFontWeights.ExtraBold,
        fontSize = SextouPrimitiveTypography.DisplayLarge,
        lineHeight = SextouPrimitiveTypography.DisplayLargeLineHeight,
    )

    val HeadlineMedium = TextStyle(
        fontFamily = SextouDisplayFont,
        fontWeight = SextouPrimitiveFontWeights.ExtraBold,
        fontSize = SextouPrimitiveTypography.HeadlineMedium,
        lineHeight = SextouPrimitiveTypography.HeadlineMediumLineHeight,
    )

    val TitleMedium = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = SextouPrimitiveFontWeights.Bold,
        fontSize = SextouPrimitiveTypography.Base,
        lineHeight = SextouPrimitiveTypography.BaseLineHeight,
    )

    val BodyLarge = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = SextouPrimitiveFontWeights.Regular,
        fontSize = SextouPrimitiveTypography.Base,
        lineHeight = SextouPrimitiveTypography.BaseLineHeight,
    )

    val LabelSmall = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = SextouPrimitiveFontWeights.SemiBold,
        fontSize = SextouPrimitiveTypography.LabelSmall,
        lineHeight = SextouPrimitiveTypography.LabelSmallLineHeight,
    )

    val Brand = TextStyle(
        fontFamily = SextouDisplayFont,
        fontWeight = SextouPrimitiveFontWeights.Bold,
        fontSize = SextouPrimitiveTypography.Xxxl,
        lineHeight = SextouPrimitiveTypography.XxxlLineHeight,
        letterSpacing = (-0.75).sp,
    )

    val BrandSubtitle = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = SextouPrimitiveFontWeights.SemiBold,
        fontSize = SextouPrimitiveTypography.Micro,
        lineHeight = SextouPrimitiveTypography.MicroLineHeight,
        letterSpacing = 1.067.sp,
    )

    val SectionTitle = TextStyle(
        fontFamily = SextouDisplayFont,
        fontWeight = SextouPrimitiveFontWeights.ExtraBold,
        fontSize = SextouPrimitiveTypography.Sm,
        lineHeight = SextouPrimitiveTypography.SmLineHeight,
    )

    val CardTitle = TextStyle(
        fontFamily = SextouDisplayFont,
        fontWeight = SextouPrimitiveFontWeights.ExtraBold,
        fontSize = SextouPrimitiveTypography.Base,
        lineHeight = SextouPrimitiveTypography.SmLineHeight,
    )

    val Search = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = SextouPrimitiveFontWeights.SemiBold,
        fontSize = SextouPrimitiveTypography.Sm,
        lineHeight = SextouPrimitiveTypography.SmLineHeight,
    )

    val InputLabel = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = SextouPrimitiveFontWeights.Bold,
        fontSize = SextouPrimitiveTypography.Input,
        lineHeight = SextouPrimitiveTypography.InputLineHeight,
    )

    val InputSupporting = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = SextouPrimitiveFontWeights.Regular,
        fontSize = SextouPrimitiveTypography.Input,
        lineHeight = SextouPrimitiveTypography.InputLineHeight,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
    )

    val InputError = InputSupporting.copy(
        fontWeight = SextouPrimitiveFontWeights.Bold,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Normal,
    )

    val Category = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = SextouPrimitiveFontWeights.SemiBold,
        fontSize = SextouPrimitiveTypography.LabelSmall,
        lineHeight = SextouPrimitiveTypography.LabelSmallLineHeight,
        letterSpacing = 0.6.sp,
    )

    val Metadata = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = SextouPrimitiveFontWeights.Regular,
        fontSize = SextouPrimitiveTypography.Xs,
        lineHeight = SextouPrimitiveTypography.XsLineHeight,
    )

    val NavigationLabel = TextStyle(
        fontFamily = SextouBodyFont,
        fontWeight = SextouPrimitiveFontWeights.Bold,
        fontSize = SextouPrimitiveTypography.Sm,
        lineHeight = SextouPrimitiveTypography.SmLineHeight,
    )

    val Status = SextouCompactActionText
    val ActionButton = SextouCompactActionText
}

val SextouTypography = Typography(
    displayLarge = SextouTextStyles.DisplayLarge,
    headlineLarge = SextouTextStyles.HeadlineMedium,
    headlineMedium = SextouTextStyles.HeadlineMedium,
    titleLarge = SextouTextStyles.CardTitle,
    titleMedium = SextouTextStyles.TitleMedium,
    bodyLarge = SextouTextStyles.BodyLarge,
    bodyMedium = SextouTextStyles.Metadata,
    labelLarge = SextouTextStyles.NavigationLabel,
    labelMedium = SextouTextStyles.Metadata,
    labelSmall = SextouTextStyles.LabelSmall,
)
