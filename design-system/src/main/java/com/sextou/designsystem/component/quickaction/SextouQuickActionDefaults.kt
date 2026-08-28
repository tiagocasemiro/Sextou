package com.sextou.designsystem.component.quickaction

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouCornerRadius
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles

/** Defaults, action types and token-backed styles for [SextouQuickAction]. */
object SextouQuickActionDefaults {
    internal val Width: Dp = SextouDimensions.QuickActionWidth
    internal val Height: Dp = SextouDimensions.QuickActionHeight
    internal val IconContainerHeight: Dp = SextouDimensions.QuickActionIconContainerHeight
    internal val IconSize: Dp = SextouDimensions.QuickActionIcon
    internal val VisitIconWidth: Dp = SextouDimensions.QuickActionVisitIconWidth
    internal val IconPaddingTop: Dp = SextouDimensions.QuickActionIconPaddingTop
    internal val IconPaddingBottom: Dp = SextouDimensions.QuickActionIconPaddingBottom

    /** Semantic action represented by a quick-action tile. */
    enum class Action {
        FAVORITAR,
        VISITAR,
        IGNORAR,
    }

    /** Visual contract shared by all quick-action actions. */
    @Immutable
    data class Style(
        val containerColor: Color,
        val borderColor: Color,
        val borderWidth: Dp,
        val shape: Shape,
        val emphasisIconColor: Color,
        val mutedIconColor: Color,
        val labelColor: Color,
        val labelTextStyle: TextStyle,
        val rippleColor: Color,
        val verticalPadding: Dp,
        val iconLabelSpacing: Dp,
    )

    /** Returns the default token-backed style for the quick-action tile. */
    @Composable
    fun defaultStyle(): Style = Style(
        containerColor = SextouColors.SurfaceContainer,
        borderColor = SextouColors.OutlineVariant,
        borderWidth = SextouDimensions.Border,
        shape = RoundedCornerShape(SextouCornerRadius.Medium),
        emphasisIconColor = SextouColors.Primary,
        mutedIconColor = SextouColors.TextSecondary,
        labelColor = SextouColors.TextSecondary,
        labelTextStyle = SextouTextStyles.QuickAction,
        rippleColor = SextouColors.TextPrimary,
        verticalPadding = SextouSpacing.Md,
        iconLabelSpacing = SextouSpacing.Sm,
    )

    internal fun iconWidth(action: Action): Dp = when (action) {
        Action.FAVORITAR, Action.IGNORAR -> IconSize
        Action.VISITAR -> VisitIconWidth
    }
}
