package com.sextou.designsystem.component.button

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouCornerRadius
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles

/** Defaults, metrics and token-backed styles for [SextouButton]. */
object SextouButtonDefaults {
    internal val IconGap = SextouSpacing.Sm
    internal val IconSize = SextouDimensions.ButtonIcon
    internal val DefaultBorderWidth = SextouDimensions.Border
    internal val InteractionDurationMillis = 200
    internal const val InteractionScale = 1.05f
    internal const val RestingScale = 1f
    internal val InteractionEasing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
    internal val NoBorder = SextouCornerRadius.None

    /** Visual sizes supported by the button component. */
    enum class Size {
        Large,
        Medium,
        Small,
    }

    /** Visual contract shared by every button style and interaction state. */
    @Immutable
    data class Style(
        val containerColor: Color,
        val interactionContainerColor: Color,
        val disabledContainerColor: Color,
        val contentColor: Color,
        val interactionContentColor: Color,
        val disabledContentColor: Color,
        val borderColor: Color,
        val interactionBorderColor: Color,
        val disabledBorderColor: Color,
        val borderWidth: Dp,
        val rippleColor: Color,
    )

    /** Returns the primary token-backed button style. */
    @Composable
    fun primaryStyle(): Style = Style(
        containerColor = SextouColors.Primary,
        interactionContainerColor = SextouColors.Accent,
        disabledContainerColor = SextouColors.ButtonDisabledContainer,
        contentColor = SextouColors.OnPrimary,
        interactionContentColor = SextouColors.OnPrimary,
        disabledContentColor = SextouColors.ButtonDisabledContent,
        borderColor = Color.Transparent,
        interactionBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        borderWidth = NoBorder,
        rippleColor = SextouColors.OnPrimary,
    )

    /** Returns the secondary token-backed button style. */
    @Composable
    fun secondaryStyle(): Style = Style(
        containerColor = SextouColors.Secondary,
        interactionContainerColor = SextouColors.SecondaryHover,
        disabledContainerColor = SextouColors.ButtonDisabledContainer,
        contentColor = SextouColors.TextPrimary,
        interactionContentColor = SextouColors.TextPrimary,
        disabledContentColor = SextouColors.ButtonDisabledContent,
        borderColor = Color.Transparent,
        interactionBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        borderWidth = NoBorder,
        rippleColor = SextouColors.TextPrimary,
    )

    /** Returns the outline token-backed button style. */
    @Composable
    fun outlineStyle(): Style = Style(
        containerColor = Color.Transparent,
        interactionContainerColor = SextouColors.ButtonHoverSurface,
        disabledContainerColor = Color.Transparent,
        contentColor = SextouColors.TextPrimary,
        interactionContentColor = SextouColors.TextPrimary,
        disabledContentColor = SextouColors.ButtonDisabledContent,
        borderColor = SextouColors.ButtonOutlineBorder,
        interactionBorderColor = SextouColors.ButtonOutlineBorderHover,
        disabledBorderColor = SextouColors.ButtonOutlineBorderDisabled,
        borderWidth = DefaultBorderWidth,
        rippleColor = SextouColors.TextPrimary,
    )

    /** Returns the ghost token-backed button style. */
    @Composable
    fun ghostStyle(): Style = Style(
        containerColor = Color.Transparent,
        interactionContainerColor = SextouColors.ButtonHoverSurface,
        disabledContainerColor = Color.Transparent,
        contentColor = SextouColors.TextSecondary,
        interactionContentColor = SextouColors.TextPrimary,
        disabledContentColor = SextouColors.ButtonDisabledContent,
        borderColor = Color.Transparent,
        interactionBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        borderWidth = NoBorder,
        rippleColor = SextouColors.TextPrimary,
    )

    internal fun height(size: Size): Dp = when (size) {
        Size.Large -> SextouDimensions.ButtonLargeHeight
        Size.Medium -> SextouDimensions.ButtonMediumHeight
        Size.Small -> SextouDimensions.ButtonSmallHeight
    }

    internal fun horizontalPadding(size: Size): Dp = when (size) {
        Size.Large -> SextouDimensions.ButtonLargeHorizontalPadding
        Size.Medium -> SextouDimensions.ButtonMediumHorizontalPadding
        Size.Small -> SextouDimensions.ButtonSmallHorizontalPadding
    }

    internal fun shape(size: Size) = when (size) {
        Size.Large -> androidx.compose.foundation.shape.RoundedCornerShape(SextouCornerRadius.ButtonLarge)
        Size.Medium -> androidx.compose.foundation.shape.RoundedCornerShape(SextouCornerRadius.Medium)
        Size.Small -> androidx.compose.foundation.shape.RoundedCornerShape(SextouCornerRadius.Base)
    }

    internal fun textStyle(size: Size): TextStyle = when (size) {
        Size.Large -> SextouTextStyles.TitleMedium
        Size.Medium -> SextouTextStyles.NavigationLabel
        Size.Small -> SextouTextStyles.ButtonSmall
    }
}
