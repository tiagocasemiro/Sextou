package com.sextou.designsystem.component.filtersheet

import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sextou.designsystem.component.button.SextouButtonDefaults
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouCornerRadius
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouElevation
import com.sextou.designsystem.theme.SextouPrimitiveAlpha
import com.sextou.designsystem.theme.SextouShapes
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles

/** Defaults and token-backed styles for [SextouFilterSheet]. */
object SextouFilterSheetDefaults {
    internal val sheetHorizontalPadding = SextouDimensions.FilterSheetHorizontalPadding
    internal val sheetHandleWidth = SextouDimensions.FilterSheetHandleWidth
    internal val sheetHandleHeight = SextouDimensions.FilterSheetHandleHeight
    internal val sheetHandleAreaHeight = SextouDimensions.FilterSheetHandleAreaHeight
    internal val sheetCloseButtonSize = SextouDimensions.FilterSheetCloseButton
    internal val sheetCloseIconSize = SextouDimensions.FilterSheetCloseIcon
    internal val sheetSelectionIconSize = SextouDimensions.FilterSheetSelectionIcon
    internal val sheetChipHeight = SextouDimensions.FilterSheetChipHeight
    internal val sheetPriceCardHeight = SextouDimensions.FilterSheetPriceCardHeight
    internal val sheetOptionTouchTarget = SextouDimensions.FilterSheetOptionTouchTarget
    internal val sheetHeaderHeight = 57.dp
    internal val sheetFooterHeight = 88.dp
    internal val sheetSectionPadding = SextouSpacing.Lg
    internal val sheetContentGap = SextouSpacing.Md
    internal val sheetOptionGap = SextouSpacing.Sm
    internal val sheetDividerThickness = SextouDimensions.SectionDividerThickness
    internal val sheetBorderWidth = SextouDimensions.Border

    /** Presentation layout used by a group of filter options. */
    enum class GroupLayout {
        CHIPS,
        PRICE_CARDS,
        SWITCHES,
    }

    /** Generic option rendered by the filter sheet. */
    @Immutable
    data class OptionData(
        val id: String,
        val label: String,
        val supportingText: String? = null,
    )

    /** Ordered group of generic options rendered by the filter sheet. */
    @Immutable
    data class GroupData(
        val id: String,
        val title: String,
        val layout: GroupLayout,
        val options: List<OptionData>,
    )

    /** Visual contract shared by the sheet, its controls and its action. */
    @Immutable
    data class Style(
        val sheetColor: Color,
        val scrimColor: Color,
        val contentColor: Color,
        val secondaryContentColor: Color,
        val handleColor: Color,
        val dividerColor: Color,
        val titleTextStyle: TextStyle,
        val sectionTitleTextStyle: TextStyle,
        val optionTextStyle: TextStyle,
        val priceSymbolTextStyle: TextStyle,
        val priceSupportingTextStyle: TextStyle,
        val optionContainerColor: Color,
        val selectedOptionContainerColor: Color,
        val disabledOptionContainerColor: Color,
        val optionContentColor: Color,
        val selectedOptionContentColor: Color,
        val disabledOptionContentColor: Color,
        val optionBorderColor: Color,
        val selectedOptionBorderColor: Color,
        val disabledOptionBorderColor: Color,
        val sheetShape: Shape,
        val optionShape: Shape,
        val priceCardShape: Shape,
        val closeButtonShape: Shape,
        val closeContainerColor: Color,
        val closeIconColor: Color,
        val switchColors: SwitchColors,
        val buttonStyle: SextouButtonDefaults.Style,
        val sheetTonalElevation: Dp,
        val optionBorderWidth: Dp,
    )

    /** Returns the default dark-first Sextou filter sheet style. */
    @Composable
    fun defaultStyle(): Style {
        val disabledOptionContainerColor = SextouColors.SurfaceElevated.copy(
            alpha = SextouPrimitiveAlpha.ButtonDisabled,
        )
        val disabledOptionBorderColor = SextouColors.Border.copy(
            alpha = SextouPrimitiveAlpha.ButtonDisabled,
        )
        return Style(
            sheetColor = SextouColors.Surface,
            scrimColor = SextouColors.Scrim,
            contentColor = SextouColors.TextPrimary,
            secondaryContentColor = SextouColors.TextSecondary,
            handleColor = SextouColors.SurfaceElevated,
            dividerColor = SextouColors.Divider,
            titleTextStyle = SextouTextStyles.TitleMedium,
            sectionTitleTextStyle = SextouTextStyles.Category,
            optionTextStyle = SextouTextStyles.Search,
            priceSymbolTextStyle = SextouTextStyles.Search,
            priceSupportingTextStyle = SextouTextStyles.Metadata,
            optionContainerColor = SextouColors.SurfaceImage,
            selectedOptionContainerColor = SextouColors.Primary,
            disabledOptionContainerColor = disabledOptionContainerColor,
            optionContentColor = SextouColors.TextPrimary,
            selectedOptionContentColor = SextouColors.OnPrimary,
            disabledOptionContentColor = SextouColors.ButtonDisabledContent,
            optionBorderColor = SextouColors.Border,
            selectedOptionBorderColor = SextouColors.Primary,
            disabledOptionBorderColor = disabledOptionBorderColor,
            sheetShape = RoundedCornerShape(
                topStart = SextouCornerRadius.Large,
                topEnd = SextouCornerRadius.Large,
            ),
            optionShape = SextouShapes.small,
            priceCardShape = RoundedCornerShape(
                SextouCornerRadius.Input,
            ),
            closeButtonShape = CircleShape,
            closeContainerColor = SextouColors.SurfaceImage,
            closeIconColor = SextouColors.TextSecondary,
            switchColors = SwitchDefaults.colors(
                checkedThumbColor = SextouColors.OnPrimary,
                checkedTrackColor = SextouColors.Primary,
                checkedBorderColor = SextouColors.Primary,
                uncheckedThumbColor = SextouColors.TextPrimary,
                uncheckedTrackColor = SextouColors.SurfaceElevated,
                uncheckedBorderColor = SextouColors.SurfaceElevated,
                disabledCheckedThumbColor = SextouColors.ButtonDisabledContent,
                disabledCheckedTrackColor = SextouColors.ButtonDisabledContainer,
                disabledCheckedBorderColor = SextouColors.ButtonDisabledContainer,
                disabledUncheckedThumbColor = SextouColors.ButtonDisabledContent,
                disabledUncheckedTrackColor = SextouColors.ButtonDisabledContainer,
                disabledUncheckedBorderColor = SextouColors.ButtonDisabledContainer,
            ),
            buttonStyle = SextouButtonDefaults.primaryStyle(),
            sheetTonalElevation = SextouElevation.Level1,
            optionBorderWidth = sheetBorderWidth,
        )
    }
}
