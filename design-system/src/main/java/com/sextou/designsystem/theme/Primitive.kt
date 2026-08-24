package com.sextou.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Raw values from the Sextou Primitive Tokens sheet (Figma node 19:1195).
 *
 * These values have no product meaning by themselves. Product meaning belongs
 * to the semantic tokens in the other files in this package.
 */
object SextouPrimitiveColors {
    const val Orange50Argb: Long = 0xFFFFF7EDL
    const val Orange100Argb: Long = 0xFFFFEDD5L
    const val Orange200Argb: Long = 0xFFFED7AAL
    const val Orange300Argb: Long = 0xFFFDBA74L
    const val Orange400Argb: Long = 0xFFFB923CL
    const val Orange500Argb: Long = 0xFFF97316L
    const val Orange600Argb: Long = 0xFFEA580CL
    const val Orange700Argb: Long = 0xFFC2410CL
    const val Orange800Argb: Long = 0xFF9A3412L
    const val Orange900Argb: Long = 0xFF7C2D12L
    const val Orange950Argb: Long = 0xFF431407L

    const val Slate50Argb: Long = 0xFFF8FAFCL
    const val Slate100Argb: Long = 0xFFF1F5F9L
    const val Slate200Argb: Long = 0xFFE2E8F0L
    const val Slate300Argb: Long = 0xFFCBD5E1L
    const val Slate400Argb: Long = 0xFF94A3B8L
    const val Slate500Argb: Long = 0xFF64748BL
    const val Slate600Argb: Long = 0xFF475569L
    const val Slate700Argb: Long = 0xFF334155L
    const val Slate800Argb: Long = 0xFF1E293BL
    const val Slate900Argb: Long = 0xFF0F172AL
    const val Slate950Argb: Long = 0xFF020617L

    const val Emerald50Argb: Long = 0xFFECFDF5L
    const val Emerald200Argb: Long = 0xFFA7F3D0L
    const val Emerald500Argb: Long = 0xFF10B981L
    const val Emerald700Argb: Long = 0xFF047857L
    const val Emerald900Argb: Long = 0xFF064E3BL

    const val Rose50Argb: Long = 0xFFFFF1F2L
    const val Rose200Argb: Long = 0xFFFECDD3L
    const val Rose500Argb: Long = 0xFFF43F5EL
    const val Rose700Argb: Long = 0xFFBE123CL
    const val Rose900Argb: Long = 0xFF881337L

    // Product primitives surfaced by the semantic Figma sheet (node 19:785).
    const val BrandOrangeArgb: Long = 0xFFFE9A00L
    const val BrandOrangeStrongArgb: Long = 0xFFFFB900L
    const val BrandAccentArgb: Long = 0xFFFFD230L
    const val ProductBackgroundArgb: Long = 0xFF111111L
    const val ProductSurfaceArgb: Long = 0xFF1C1C1CL
    const val ProductSurfaceElevatedArgb: Long = 0xFF2A2A2AL
    const val ProductSurfaceImageArgb: Long = 0xFF262626L
    const val ProductTextPrimaryArgb: Long = 0xFFF2EDE4L
    const val ProductTextSecondaryArgb: Long = 0xFF9A9080L
    const val ProductSuccessArgb: Long = 0xFF00D492L
    const val ProductSuccessStrongArgb: Long = 0xFF00BC7DL
    const val ProductErrorArgb: Long = 0xFFFF5722L
    const val ProductSecondaryHoverArgb: Long = 0xFFFF7043L
    const val ProductClosedContainerArgb: Long = 0xFF3F3F47L
    const val ProductClosedIndicatorArgb: Long = 0xFF9F9FA9L
    const val ProductClosedContentArgb: Long = 0xFFD4D4D8L

    const val WhiteArgb: Long = 0xFFFFFFFFL
    const val BlackArgb: Long = 0xFF000000L

    private fun color(argb: Long): Color = Color(argb.toULong())

    val Orange50 = color(Orange50Argb)
    val Orange100 = color(Orange100Argb)
    val Orange200 = color(Orange200Argb)
    val Orange300 = color(Orange300Argb)
    val Orange400 = color(Orange400Argb)
    val Orange500 = color(Orange500Argb)
    val Orange600 = color(Orange600Argb)
    val Orange700 = color(Orange700Argb)
    val Orange800 = color(Orange800Argb)
    val Orange900 = color(Orange900Argb)
    val Orange950 = color(Orange950Argb)

    val Slate50 = color(Slate50Argb)
    val Slate100 = color(Slate100Argb)
    val Slate200 = color(Slate200Argb)
    val Slate300 = color(Slate300Argb)
    val Slate400 = color(Slate400Argb)
    val Slate500 = color(Slate500Argb)
    val Slate600 = color(Slate600Argb)
    val Slate700 = color(Slate700Argb)
    val Slate800 = color(Slate800Argb)
    val Slate900 = color(Slate900Argb)
    val Slate950 = color(Slate950Argb)

    val Emerald50 = color(Emerald50Argb)
    val Emerald200 = color(Emerald200Argb)
    val Emerald500 = color(Emerald500Argb)
    val Emerald700 = color(Emerald700Argb)
    val Emerald900 = color(Emerald900Argb)

    val Rose50 = color(Rose50Argb)
    val Rose200 = color(Rose200Argb)
    val Rose500 = color(Rose500Argb)
    val Rose700 = color(Rose700Argb)
    val Rose900 = color(Rose900Argb)

    val BrandOrange = color(BrandOrangeArgb)
    val BrandOrangeStrong = color(BrandOrangeStrongArgb)
    val BrandAccent = color(BrandAccentArgb)
    val ProductBackground = color(ProductBackgroundArgb)
    val ProductSurface = color(ProductSurfaceArgb)
    val ProductSurfaceElevated = color(ProductSurfaceElevatedArgb)
    val ProductSurfaceImage = color(ProductSurfaceImageArgb)
    val ProductTextPrimary = color(ProductTextPrimaryArgb)
    val ProductTextSecondary = color(ProductTextSecondaryArgb)
    val ProductSuccess = color(ProductSuccessArgb)
    val ProductSuccessStrong = color(ProductSuccessStrongArgb)
    val ProductError = color(ProductErrorArgb)
    val ProductSecondaryHover = color(ProductSecondaryHoverArgb)
    val ProductClosedContainer = color(ProductClosedContainerArgb)
    val ProductClosedIndicator = color(ProductClosedIndicatorArgb)
    val ProductClosedContent = color(ProductClosedContentArgb)
    val White = color(WhiteArgb)
    val Black = color(BlackArgb)
}

object SextouPrimitiveAlpha {
    const val None = 0f
    const val Subtle = 0.08f
    const val Medium = 0.16f
    const val Strong = 0.32f
    const val High = 0.64f
    const val NearOpaque = 0.85f
    const val StatusContainer = 0.9f
    const val StatusIndicator = 0.59f
    const val StatusUnavailable = 0.3f
    const val TextMuted = 0.4f
    const val ButtonDisabled = 0.5f
    const val ButtonDisabledContent = 0.3f
    const val ButtonBorder = 0.2f
    const val ButtonBorderHover = 0.4f
    const val ButtonBorderDisabled = 0.1f
    const val ButtonHoverSurface = 0.05f
    const val Scrim = 0.6f
}

object SextouPrimitiveSpacing {
    val None = 0.dp
    val Xxs = 2.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Base = 16.dp
    val Lg = 24.dp
    val Xl = 32.dp
    val Xxl = 48.dp
    val Xxxl = 64.dp
    val Huge = 128.dp
}

object SextouPrimitiveCornerRadius {
    val None = 0.dp
    val Small = 4.dp
    val Base = 8.dp
    val Medium = 12.dp
    val Large = 24.dp
    val Full = 9999.dp
}

object SextouPrimitiveFontWeights {
    val Regular = FontWeight.Normal
    val Medium = FontWeight.Medium
    val SemiBold = FontWeight.SemiBold
    val Bold = FontWeight.Bold
    val ExtraBold = FontWeight.ExtraBold
    val Black = FontWeight.Black
}

object SextouPrimitiveTypography {
    val Micro = 9.sp
    val Xs = 12.sp
    val Sm = 14.sp
    val Base = 16.sp
    val Lg = 18.sp
    val Xl = 20.sp
    val Xxl = 24.sp
    val Xxxl = 30.sp
    val FiveXl = 48.sp
    val DisplayLarge = 57.sp
    val HeadlineMedium = 28.sp
    val LabelSmall = 11.sp
    val Input = 10.sp

    val MicroLineHeight = 13.5.sp
    val XsLineHeight = 16.sp
    val SmLineHeight = 20.sp
    val BaseLineHeight = 24.sp
    val LgLineHeight = 28.sp
    val XlLineHeight = 28.sp
    val XxlLineHeight = 32.sp
    val XxxlLineHeight = 36.sp
    val FiveXlLineHeight = 48.sp
    val DisplayLargeLineHeight = 64.sp
    val HeadlineMediumLineHeight = 36.sp
    val LabelSmallLineHeight = 16.sp
    val InputLineHeight = 15.sp
    val StatusBadgeLineHeight = 18.sp
}

object SextouPrimitiveElevation {
    val Level1 = 1.dp
    val Level2 = 4.dp
    val Level3 = 10.dp
    val Level4 = 20.dp
    val Level5 = 25.dp
    val Level6 = 35.dp
}
