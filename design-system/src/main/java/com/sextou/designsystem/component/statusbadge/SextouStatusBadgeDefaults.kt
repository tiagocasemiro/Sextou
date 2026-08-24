package com.sextou.designsystem.component.statusbadge

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

/** Defaults and token-backed styles for [SextouStatusBadge]. */
object SextouStatusBadgeDefaults {
    internal val Height: Dp = SextouDimensions.StatusBadgeHeight
    internal val HorizontalPadding: Dp = SextouDimensions.StatusBadgeHorizontalPadding
    internal val VerticalPadding: Dp = SextouDimensions.StatusBadgeVerticalPadding
    internal val IndicatorSize: Dp = SextouDimensions.StatusBadgeIndicator
    internal val ContentGap: Dp = SextouSpacing.Xs
    internal val Shape: Shape = androidx.compose.foundation.shape.RoundedCornerShape(SextouCornerRadius.Full)
    internal val TextStyle: TextStyle = SextouTextStyles.StatusBadge

    /** Visual contract shared by every status badge status. */
    @Immutable
    data class Style(
        val containerColor: Color,
        val indicatorColor: Color,
        val contentColor: Color,
    )

    /** Returns the success style used for an open status. */
    @Composable
    fun openStyle(): Style = Style(
        containerColor = SextouColors.StatusOpenContainer,
        indicatorColor = SextouColors.StatusOpenIndicator,
        contentColor = SextouColors.StatusOpenContent,
    )

    /** Returns the neutral style used for a closed status. */
    @Composable
    fun closedStyle(): Style = Style(
        containerColor = SextouColors.StatusClosedContainer,
        indicatorColor = SextouColors.StatusClosedIndicator,
        contentColor = SextouColors.StatusClosedContent,
    )

    /** Returns the low-emphasis style used for an unavailable status. */
    @Composable
    fun unavailableStyle(): Style = Style(
        containerColor = SextouColors.StatusUnavailableContainer,
        indicatorColor = SextouColors.StatusUnavailableIndicator,
        contentColor = SextouColors.StatusUnavailableContent,
    )

    /** Returns the token-backed style associated with [status]. */
    @Composable
    fun styleFor(status: SextouStatus): Style = when (status) {
        SextouStatus.OPEN -> openStyle()
        SextouStatus.CLOSED -> closedStyle()
        SextouStatus.UNAVAILABLE -> unavailableStyle()
    }
}
