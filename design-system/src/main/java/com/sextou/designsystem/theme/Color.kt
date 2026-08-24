package com.sextou.designsystem.theme

/** Semantic visual tokens from the Sextou semantic sheet (Figma node 19:785). */
object SextouColors {
    /** ARGB value also used by Compose Preview's compile-time annotation. */
    const val BackgroundArgb: Long = SextouPrimitiveColors.ProductBackgroundArgb

    val Background = SextouPrimitiveColors.ProductBackground
    val Surface = SextouPrimitiveColors.ProductSurface
    val SurfaceElevated = SextouPrimitiveColors.ProductSurfaceElevated
    val SurfaceImage = SextouPrimitiveColors.ProductSurfaceImage

    val TextPrimary = SextouPrimitiveColors.ProductTextPrimary
    val TextSecondary = SextouPrimitiveColors.ProductTextSecondary
    val TextMuted = TextPrimary.copy(alpha = SextouPrimitiveAlpha.TextMuted)

    val Primary = SextouPrimitiveColors.BrandOrange
    val PrimaryStrong = SextouPrimitiveColors.BrandOrangeStrong
    val OnPrimary = SextouPrimitiveColors.Black
    val Secondary = SextouPrimitiveColors.ProductError
    val SecondaryHover = SextouPrimitiveColors.ProductSecondaryHover
    val ButtonDisabledContainer = SurfaceElevated.copy(alpha = SextouPrimitiveAlpha.ButtonDisabled)
    val ButtonDisabledContent = TextSecondary.copy(alpha = SextouPrimitiveAlpha.ButtonDisabledContent)
    val ButtonOutlineBorder = SextouPrimitiveColors.White.copy(alpha = SextouPrimitiveAlpha.ButtonBorder)
    val ButtonOutlineBorderHover = SextouPrimitiveColors.White.copy(alpha = SextouPrimitiveAlpha.ButtonBorderHover)
    val ButtonOutlineBorderDisabled = SextouPrimitiveColors.White.copy(alpha = SextouPrimitiveAlpha.ButtonBorderDisabled)
    val ButtonHoverSurface = SextouPrimitiveColors.White.copy(alpha = SextouPrimitiveAlpha.ButtonHoverSurface)

    val Positive = SextouPrimitiveColors.ProductSuccess
    val PositiveStrong = SextouPrimitiveColors.ProductSuccessStrong
    val StatusOpenContainer = PositiveStrong.copy(alpha = SextouPrimitiveAlpha.NearOpaque)
    val StatusOpenIndicator = SextouPrimitiveColors.White.copy(alpha = SextouPrimitiveAlpha.High)
    val StatusOpenContent = SextouPrimitiveColors.White
    val StatusClosedContainer = SextouPrimitiveColors.ProductClosedContainer.copy(alpha = SextouPrimitiveAlpha.NearOpaque)
    val StatusClosedIndicator = SextouPrimitiveColors.ProductClosedIndicator
    val StatusClosedContent = SextouPrimitiveColors.ProductClosedContent
    val Accent = SextouPrimitiveColors.BrandAccent
    val Error = SextouPrimitiveColors.ProductError

    val Border = SextouPrimitiveColors.White.copy(alpha = SextouPrimitiveAlpha.Subtle)
    val Divider = Border
    val Scrim = SextouPrimitiveColors.Black.copy(alpha = SextouPrimitiveAlpha.Scrim)
}

internal object SextouLightColors {
    val PrimaryContainer = SextouPrimitiveColors.Orange100
    val OnPrimaryContainer = SextouPrimitiveColors.Orange950
    val SecondaryContainer = SextouPrimitiveColors.Orange200
    val OnSecondaryContainer = SextouPrimitiveColors.Orange950
    val TertiaryContainer = SextouPrimitiveColors.Emerald200
    val OnTertiaryContainer = SextouPrimitiveColors.Emerald900
    val Background = SextouPrimitiveColors.Orange50
    val OnBackground = SextouPrimitiveColors.Slate900
    val Surface = SextouPrimitiveColors.Orange50
    val OnSurface = SextouPrimitiveColors.Slate900
    val SurfaceVariant = SextouPrimitiveColors.Slate100
    val OnSurfaceVariant = SextouPrimitiveColors.Slate600
    val Outline = SextouPrimitiveColors.Slate500
    val OutlineVariant = SextouPrimitiveColors.Slate200
    val OnError = SextouPrimitiveColors.White
}
