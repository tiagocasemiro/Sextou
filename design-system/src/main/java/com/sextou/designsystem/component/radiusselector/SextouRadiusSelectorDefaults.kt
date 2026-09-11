package com.sextou.designsystem.component.radiusselector

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouSpacing

object SextouRadiusSelectorDefaults {
    internal val optionHeight = SextouDimensions.CompactIconButtonTouchTarget
    internal val optionGap = SextouSpacing.Sm

    @Immutable
    data class OptionData(val id: Int, val label: String)

    @Immutable
    data class Style(
        val containerColor: Color,
        val contentColor: Color,
        val errorColor: Color,
        val titleTextStyle: TextStyle,
        val optionTextStyle: TextStyle,
        val shape: Shape,
        val rippleColor: Color,
    )

    @Composable
    fun defaultStyle() = Style(
        containerColor = SextouColors.SurfaceElevated,
        contentColor = SextouColors.TextPrimary,
        errorColor = MaterialTheme.colorScheme.error,
        titleTextStyle = MaterialTheme.typography.headlineSmall,
        optionTextStyle = MaterialTheme.typography.bodyLarge,
        shape = MaterialTheme.shapes.extraLarge,
        rippleColor = MaterialTheme.colorScheme.primary,
    )
}
