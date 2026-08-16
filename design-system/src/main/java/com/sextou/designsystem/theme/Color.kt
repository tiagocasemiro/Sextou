package com.sextou.designsystem.theme

import androidx.compose.ui.graphics.Color

/** Semantic visual tokens extracted from the Home screen in Figma. */
object SextouColors {
    /** ARGB value also used by Compose Preview's compile-time annotation. */
    const val BackgroundArgb: Long = 0xFF111111L

    val Background = Color(BackgroundArgb.toULong())
    val Surface = Color(0xFF1C1C1C)
    val SurfaceElevated = Color(0xFF2A2A2A)
    val SurfaceImage = Color(0xFF262626)

    val TextPrimary = Color(0xFFF2EDE4)
    val TextSecondary = Color(0xFF9A9080)
    val TextMuted = Color(0x99F2EDE4)

    val Primary = Color(0xFFFE9A00)
    val PrimaryStrong = Color(0xFFFFB900)
    val OnPrimary = Color(0xFF111111)

    val Positive = Color(0xFF00D492)
    val PositiveStrong = Color(0xFF00BC7D)
    val Accent = Color(0xFFFFD230)
    val Error = Color(0xFFFF5722)

    val Border = Color(0x14FFFFFF)
    val Divider = Color(0x14FFFFFF)
    val Scrim = Color(0x99000000)
}

internal object SextouLightColors {
    val PrimaryContainer = Color(0xFFFFE0B2)
    val OnPrimaryContainer = Color(0xFF2B1700)
    val SecondaryContainer = Color(0xFFFFE7A8)
    val OnSecondaryContainer = Color(0xFF241A00)
    val TertiaryContainer = Color(0xFFB9F4D4)
    val OnTertiaryContainer = Color(0xFF002113)
    val Background = Color(0xFFFFF9F3)
    val OnBackground = Color(0xFF211A14)
    val Surface = Color(0xFFFFF9F3)
    val OnSurface = Color(0xFF211A14)
    val SurfaceVariant = Color(0xFFF1E5D8)
    val OnSurfaceVariant = Color(0xFF625B52)
    val Outline = Color(0xFF85746A)
    val OutlineVariant = Color(0xFFDAC9BB)
    val OnError = Color(0xFFFFFFFF)
}
