package com.sextou.designsystem.component.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouInputShapes
import com.sextou.designsystem.theme.SextouPrimitiveAlpha
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles

/** Defaults, layouts and visual styles for [SextouInput]. */
object SextouInputDefaults {
    internal val LabelStartPadding = SextouSpacing.Xs
    internal val SupportingStartPadding = SextouSpacing.Xs
    internal val LabelFieldGap = SextouSpacing.Sm
    internal val FieldSupportingGap = SextouSpacing.Sm
    internal val ElementGap = SextouSpacing.Md
    internal val HorizontalPadding = SextouSpacing.Lg
    internal val TextInputHeight = SextouDimensions.InputHeight
    internal val SearchBarHeight = SextouDimensions.SearchBarHeight
    internal val IconSize = SextouDimensions.InputIcon
    internal val ActionButtonSize = SextouDimensions.InputActionButton
    internal val ActionTouchTarget = SextouDimensions.InputActionTouchTarget
    internal val DefaultBorderWidth = SextouDimensions.Border
    internal val FocusedBorderWidth = SextouDimensions.InputFocusBorder

    /** Visual layouts supported by the input component. */
    enum class Layout {
        TextInput,
        SearchBar,
    }

    /** Theme-backed visual contract used by all input layouts and states. */
    @Immutable
    data class Style(
        val textStyle: TextStyle,
        val labelStyle: TextStyle,
        val supportingStyle: TextStyle,
        val errorStyle: TextStyle,
        val containerColor: Color,
        val focusedContainerColor: Color,
        val disabledContainerColor: Color,
        val contentColor: Color,
        val placeholderColor: Color,
        val disabledContentColor: Color,
        val labelColor: Color,
        val supportingColor: Color,
        val errorColor: Color,
        val borderColor: Color,
        val focusedBorderColor: Color,
        val disabledBorderColor: Color,
        val cursorColor: Color,
        val leadingIconColor: Color,
        val actionColor: Color,
        val disabledActionColor: Color,
        val actionContentColor: Color,
        val textInputShape: Shape,
        val searchBarShape: Shape,
        val actionShape: Shape,
        val rippleColor: Color,
    )

    /** Returns the default token-backed style for the component. */
    @Composable
    fun defaultStyle(): Style = Style(
        textStyle = SextouTextStyles.Search,
        labelStyle = SextouTextStyles.InputLabel,
        supportingStyle = SextouTextStyles.InputSupporting,
        errorStyle = SextouTextStyles.InputError,
        containerColor = SextouColors.SurfaceElevated,
        focusedContainerColor = SextouColors.Background,
        disabledContainerColor = SextouColors.Background,
        contentColor = SextouColors.TextPrimary,
        placeholderColor = SextouColors.TextSecondary,
        disabledContentColor = SextouColors.TextMuted,
        labelColor = SextouColors.TextSecondary,
        supportingColor = SextouColors.TextSecondary,
        errorColor = SextouColors.Error,
        borderColor = SextouColors.Border,
        focusedBorderColor = SextouColors.Primary,
        disabledBorderColor = SextouColors.Border.copy(alpha = SextouPrimitiveAlpha.TextMuted),
        cursorColor = SextouColors.Primary,
        leadingIconColor = SextouColors.TextSecondary,
        actionColor = SextouColors.Primary,
        disabledActionColor = SextouColors.SurfaceElevated,
        actionContentColor = SextouColors.OnPrimary,
        textInputShape = SextouInputShapes.TextInput,
        searchBarShape = SextouInputShapes.SearchBar,
        actionShape = SextouInputShapes.Action,
        rippleColor = SextouColors.Primary,
    )

    internal fun height(layout: Layout): Dp = when (layout) {
        Layout.TextInput -> TextInputHeight
        Layout.SearchBar -> SearchBarHeight
    }

    internal fun shape(layout: Layout, style: Style): Shape = when (layout) {
        Layout.TextInput -> style.textInputShape
        Layout.SearchBar -> style.searchBarShape
    }

    internal fun containerColor(focused: Boolean, enabled: Boolean, style: Style): Color = when {
        !enabled -> style.disabledContainerColor
        focused -> style.focusedContainerColor
        else -> style.containerColor
    }

    internal fun borderColor(focused: Boolean, isError: Boolean, enabled: Boolean, style: Style): Color = when {
        !enabled -> style.disabledBorderColor
        isError -> style.errorColor
        focused -> style.focusedBorderColor
        else -> style.borderColor
    }

    internal fun borderWidth(focused: Boolean, enabled: Boolean): Dp = if (focused && enabled) {
        FocusedBorderWidth
    } else {
        DefaultBorderWidth
    }

    internal fun contentColor(enabled: Boolean, style: Style): Color = when {
        !enabled -> style.disabledContentColor
        else -> style.contentColor
    }
}
