package com.sextou.designsystem.component.filtersheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.sextou.designsystem.R
import com.sextou.designsystem.component.button.SextouButton
import com.sextou.designsystem.component.button.SextouButtonDefaults
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouPrimitiveAlpha
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTheme

/**
 * Displays a generic, controlled filter panel in a modal bottom sheet.
 *
 * The component owns only modal and scrolling mechanics. Selection, applying
 * the draft and dismissing the sheet remain controlled by the caller.
 *
 * @param title Text announced as the sheet heading.
 * @param groups Ordered groups and options rendered by the sheet.
 * @param selectedIds IDs that are currently selected.
 * @param applyLabel Text displayed by the primary action.
 * @param closeContentDescription Accessible description for the close action.
 * @param onSelectionChanged Callback with an option ID and its next selection.
 * @param onApply Callback invoked by the primary action.
 * @param onDismiss Callback invoked by any modal dismissal mechanism.
 * @param modifier Modifier applied to the modal sheet.
 * @param enabled Enables option selection and applying while preserving dismiss.
 * @param style Token-backed visual style for the sheet.
 *
 * @see SextouFilterSheetDefaults
 * @see SextouFilterSheetDefaults.GroupData
 * @see SextouFilterSheetDefaults.GroupLayout
 * @see SextouFilterSheetDefaults.Style
 */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SextouFilterSheet(
    title: String,
    groups: List<SextouFilterSheetDefaults.GroupData>,
    selectedIds: Set<String>,
    applyLabel: String,
    closeContentDescription: String,
    onSelectionChanged: (String, Boolean) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: SextouFilterSheetDefaults.Style = SextouFilterSheetDefaults.defaultStyle(),
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        shape = style.sheetShape,
        containerColor = style.sheetColor,
        contentColor = style.contentColor,
        tonalElevation = style.sheetTonalElevation,
        scrimColor = style.scrimColor,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SextouFilterSheetDefaults.sheetHandleAreaHeight),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = SextouSpacing.Md, bottom = SextouSpacing.Xs)
                        .width(SextouFilterSheetDefaults.sheetHandleWidth)
                        .height(SextouFilterSheetDefaults.sheetHandleHeight)
                        .clip(style.optionShape)
                        .background(style.handleColor),
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding(),
        ) {
            FilterSheetHeader(
                title = title,
                closeContentDescription = closeContentDescription,
                onDismiss = onDismiss,
                style = style,
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val bodyModifier = if (maxHeight != Dp.Infinity) {
                    Modifier.heightIn(
                        max = (
                            maxHeight -
                                SextouFilterSheetDefaults.sheetHeaderHeight -
                                SextouFilterSheetDefaults.sheetFooterHeight -
                                SextouFilterSheetDefaults.sheetHandleAreaHeight
                            ).coerceAtLeast(Dp.Hairline),
                    )
                } else {
                    Modifier
                }

                Column(
                    modifier = bodyModifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(horizontal = SextouFilterSheetDefaults.sheetHorizontalPadding),
                ) {
                    groups.forEachIndexed { index, group ->
                        if (index > 0) {
                            FilterSheetDivider(style = style)
                        }
                        FilterSheetGroup(
                            group = group,
                            selectedIds = selectedIds,
                            enabled = enabled,
                            onSelectionChanged = onSelectionChanged,
                            style = style,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = SextouFilterSheetDefaults.sheetHorizontalPadding,
                        top = SextouSpacing.Xs,
                        end = SextouFilterSheetDefaults.sheetHorizontalPadding,
                        bottom = SextouSpacing.Lg,
                    ),
            ) {
                SextouButton(
                    label = applyLabel,
                    onClick = onApply,
                    modifier = Modifier.fillMaxWidth(),
                    size = SextouButtonDefaults.Size.Large,
                    leadingIcon = painterResource(R.drawable.ic_sextou_check),
                    enabled = enabled,
                    style = style.buttonStyle,
                )
            }
        }
    }
}

@Composable
private fun FilterSheetHeader(
    title: String,
    closeContentDescription: String,
    onDismiss: () -> Unit,
    style: SextouFilterSheetDefaults.Style,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(SextouFilterSheetDefaults.sheetHeaderHeight)
                .padding(horizontal = SextouFilterSheetDefaults.sheetHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = style.titleTextStyle,
                color = style.contentColor,
                modifier = Modifier.semantics { heading() },
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(SextouFilterSheetDefaults.sheetOptionTouchTarget),
            ) {
                Box(
                    modifier = Modifier
                        .size(SextouFilterSheetDefaults.sheetCloseButtonSize)
                        .clip(style.closeButtonShape)
                        .background(style.closeContainerColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sextou_close),
                        contentDescription = closeContentDescription,
                        modifier = Modifier.size(SextouFilterSheetDefaults.sheetCloseIconSize),
                        tint = style.closeIconColor,
                    )
                }
            }
        }
        FilterSheetDivider(style = style)
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun FilterSheetGroup(
    group: SextouFilterSheetDefaults.GroupData,
    selectedIds: Set<String>,
    enabled: Boolean,
    onSelectionChanged: (String, Boolean) -> Unit,
    style: SextouFilterSheetDefaults.Style,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SextouFilterSheetDefaults.sheetSectionPadding),
        verticalArrangement = Arrangement.spacedBy(SextouFilterSheetDefaults.sheetContentGap),
    ) {
        Text(
            text = group.title,
            style = style.sectionTitleTextStyle,
            color = style.secondaryContentColor,
        )
        when (group.layout) {
            SextouFilterSheetDefaults.GroupLayout.CHIPS -> {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        SextouFilterSheetDefaults.sheetOptionGap,
                    ),
                    verticalArrangement = Arrangement.spacedBy(
                        SextouFilterSheetDefaults.sheetOptionGap,
                    ),
                ) {
                    group.options.forEach { option ->
                        FilterSheetChip(
                            option = option,
                            selected = option.id in selectedIds,
                            enabled = enabled,
                            onSelectionChanged = onSelectionChanged,
                            style = style,
                        )
                    }
                }
            }

            SextouFilterSheetDefaults.GroupLayout.PRICE_CARDS -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        SextouFilterSheetDefaults.sheetOptionGap,
                    ),
                ) {
                    group.options.forEach { option ->
                        FilterSheetPriceCard(
                            option = option,
                            selected = option.id in selectedIds,
                            enabled = enabled,
                            onSelectionChanged = onSelectionChanged,
                            style = style,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            SextouFilterSheetDefaults.GroupLayout.SWITCHES -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SextouFilterSheetDefaults.sheetOptionGap),
                ) {
                    group.options.forEach { option ->
                        FilterSheetSwitch(
                            option = option,
                            selected = option.id in selectedIds,
                            enabled = enabled,
                            onSelectionChanged = onSelectionChanged,
                            style = style,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSheetChip(
    option: SextouFilterSheetDefaults.OptionData,
    selected: Boolean,
    enabled: Boolean,
    onSelectionChanged: (String, Boolean) -> Unit,
    style: SextouFilterSheetDefaults.Style,
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectionChanged(option.id, !selected) },
        modifier = Modifier.heightIn(
            min = maxOf(
                SextouFilterSheetDefaults.sheetChipHeight,
                SextouFilterSheetDefaults.sheetOptionTouchTarget,
            ),
        ),
        enabled = enabled,
        shape = style.optionShape,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = style.optionContainerColor,
            labelColor = style.optionContentColor,
            selectedContainerColor = style.selectedOptionContainerColor,
            selectedLabelColor = style.selectedOptionContentColor,
            disabledContainerColor = style.disabledOptionContainerColor,
            disabledLabelColor = style.disabledOptionContentColor,
        ),
        border = BorderStroke(
            width = style.optionBorderWidth,
            color = when {
                !enabled -> style.disabledOptionBorderColor
                selected -> style.selectedOptionBorderColor
                else -> style.optionBorderColor
            },
        ),
        leadingIcon = if (selected) {
            {
                Icon(
                    painter = painterResource(R.drawable.ic_sextou_check),
                    contentDescription = null,
                    modifier = Modifier.size(SextouFilterSheetDefaults.sheetSelectionIconSize),
                    tint = style.selectedOptionContentColor,
                )
            }
        } else {
            null
        },
        label = {
            Text(
                text = option.label,
                style = style.optionTextStyle,
            )
        },
    )
}

@Composable
private fun FilterSheetPriceCard(
    option: SextouFilterSheetDefaults.OptionData,
    selected: Boolean,
    enabled: Boolean,
    onSelectionChanged: (String, Boolean) -> Unit,
    style: SextouFilterSheetDefaults.Style,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val containerColor = when {
        !enabled -> style.disabledOptionContainerColor
        selected -> style.selectedOptionContainerColor
        else -> style.optionContainerColor
    }
    val contentColor = when {
        !enabled -> style.disabledOptionContentColor
        selected -> style.selectedOptionContentColor
        else -> style.optionContentColor
    }

    Column(
        modifier = modifier
            .heightIn(min = SextouFilterSheetDefaults.sheetPriceCardHeight)
            .clip(style.priceCardShape)
            .background(containerColor, style.priceCardShape)
            .border(
                BorderStroke(
                    style.optionBorderWidth,
                    when {
                        !enabled -> style.disabledOptionBorderColor
                        selected -> style.selectedOptionBorderColor
                        else -> style.optionBorderColor
                    },
                ),
                style.priceCardShape,
            )
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = rememberRipple(color = style.selectedOptionBorderColor, bounded = true),
                onClick = { onSelectionChanged(option.id, !selected) },
            )
            .padding(vertical = SextouSpacing.Md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = option.label,
            style = style.priceSymbolTextStyle,
            color = contentColor,
        )
        option.supportingText?.let { supportingText ->
            Text(
                text = supportingText,
                style = style.priceSupportingTextStyle,
                color = contentColor.copy(alpha = SextouPrimitiveAlpha.StatusIndicator),
            )
        }
    }
}

@Composable
private fun FilterSheetSwitch(
    option: SextouFilterSheetDefaults.OptionData,
    selected: Boolean,
    enabled: Boolean,
    onSelectionChanged: (String, Boolean) -> Unit,
    style: SextouFilterSheetDefaults.Style,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = SextouFilterSheetDefaults.sheetOptionTouchTarget)
            .clickable(
                enabled = enabled,
                role = Role.Switch,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = style.selectedOptionBorderColor, bounded = true),
                onClick = { onSelectionChanged(option.id, !selected) },
            )
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = option.label,
            style = style.optionTextStyle,
            color = if (enabled) style.optionContentColor else style.disabledOptionContentColor,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = selected,
            onCheckedChange = null,
            enabled = enabled,
            colors = style.switchColors,
        )
    }
}

@Composable
private fun FilterSheetDivider(style: SextouFilterSheetDefaults.Style) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SextouFilterSheetDefaults.sheetDividerThickness)
            .background(style.dividerColor),
    )
}

@Composable
private fun filterSheetPreviewGroups(): List<SextouFilterSheetDefaults.GroupData> = listOf(
    SextouFilterSheetDefaults.GroupData(
        id = "types",
        title = stringResource(R.string.filter_sheet_preview_type_title),
        layout = SextouFilterSheetDefaults.GroupLayout.CHIPS,
        options = listOf(
            SextouFilterSheetDefaults.OptionData(
                id = "type-boteco",
                label = stringResource(R.string.filter_sheet_preview_type_boteco),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "type-skewer",
                label = stringResource(R.string.filter_sheet_preview_type_skewer),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "type-wine-store",
                label = stringResource(R.string.filter_sheet_preview_type_wine_store),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "type-karaoke",
                label = stringResource(R.string.filter_sheet_preview_type_karaoke),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "type-food-trailer",
                label = stringResource(R.string.filter_sheet_preview_type_food_trailer),
            ),
        ),
    ),
    SextouFilterSheetDefaults.GroupData(
        id = "prices",
        title = stringResource(R.string.filter_sheet_preview_price_title),
        layout = SextouFilterSheetDefaults.GroupLayout.PRICE_CARDS,
        options = listOf(
            SextouFilterSheetDefaults.OptionData(
                id = "price-low",
                label = stringResource(R.string.filter_sheet_preview_price_low_symbol),
                supportingText = stringResource(R.string.filter_sheet_preview_price_low),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "price-medium",
                label = stringResource(R.string.filter_sheet_preview_price_medium_symbol),
                supportingText = stringResource(R.string.filter_sheet_preview_price_medium),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "price-high",
                label = stringResource(R.string.filter_sheet_preview_price_high_symbol),
                supportingText = stringResource(R.string.filter_sheet_preview_price_high),
            ),
        ),
    ),
    SextouFilterSheetDefaults.GroupData(
        id = "other",
        title = stringResource(R.string.filter_sheet_preview_other_title),
        layout = SextouFilterSheetDefaults.GroupLayout.SWITCHES,
        options = listOf(
            SextouFilterSheetDefaults.OptionData(
                id = "open-now",
                label = stringResource(R.string.filter_sheet_preview_open_now),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "kids-space",
                label = stringResource(R.string.filter_sheet_preview_kids_space),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "live-music",
                label = stringResource(R.string.filter_sheet_preview_live_music),
            ),
        ),
    ),
    SextouFilterSheetDefaults.GroupData(
        id = "categories",
        title = stringResource(R.string.filter_sheet_preview_category_title),
        layout = SextouFilterSheetDefaults.GroupLayout.CHIPS,
        options = listOf(
            SextouFilterSheetDefaults.OptionData(
                id = "category-karaoke",
                label = stringResource(R.string.filter_sheet_preview_category_karaoke),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "category-kids",
                label = stringResource(R.string.filter_sheet_preview_category_kids),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "category-street-food",
                label = stringResource(R.string.filter_sheet_preview_category_street_food),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "category-wine-store",
                label = stringResource(R.string.filter_sheet_preview_category_wine_store),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "category-live-music",
                label = stringResource(R.string.filter_sheet_preview_category_live_music),
            ),
            SextouFilterSheetDefaults.OptionData(
                id = "category-24-hours",
                label = stringResource(R.string.filter_sheet_preview_category_24_hours),
            ),
        ),
    ),
)

@Preview(
    name = "Sextou filter sheet",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
    widthDp = 390,
    heightDp = 844,
)
@Composable
private fun SextouFilterSheetPreview() {
    SextouTheme {
        SextouFilterSheet(
            title = stringResource(R.string.filter_sheet_preview_title),
            groups = filterSheetPreviewGroups(),
            selectedIds = emptySet(),
            applyLabel = stringResource(R.string.filter_sheet_preview_apply),
            closeContentDescription = stringResource(R.string.filter_sheet_preview_close),
            onSelectionChanged = { _, _ -> },
            onApply = {},
            onDismiss = {},
        )
    }
}

@Preview(
    name = "Sextou filter sheet selected",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
    widthDp = 390,
    heightDp = 844,
)
@Composable
private fun SextouFilterSheetSelectedPreview() {
    SextouTheme {
        SextouFilterSheet(
            title = stringResource(R.string.filter_sheet_preview_title),
            groups = filterSheetPreviewGroups(),
            selectedIds = setOf("type-boteco", "price-medium", "open-now", "category-live-music"),
            applyLabel = stringResource(R.string.filter_sheet_preview_apply),
            closeContentDescription = stringResource(R.string.filter_sheet_preview_close),
            onSelectionChanged = { _, _ -> },
            onApply = {},
            onDismiss = {},
        )
    }
}

@Preview(
    name = "Sextou filter sheet disabled",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
    widthDp = 390,
    heightDp = 844,
)
@Composable
private fun SextouFilterSheetDisabledPreview() {
    SextouTheme {
        SextouFilterSheet(
            title = stringResource(R.string.filter_sheet_preview_title),
            groups = filterSheetPreviewGroups(),
            selectedIds = setOf("type-boteco", "price-medium", "open-now"),
            applyLabel = stringResource(R.string.filter_sheet_preview_apply),
            closeContentDescription = stringResource(R.string.filter_sheet_preview_close),
            onSelectionChanged = { _, _ -> },
            onApply = {},
            onDismiss = {},
            enabled = false,
        )
    }
}

@Preview(
    name = "Sextou filter sheet large font",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
    widthDp = 320,
    heightDp = 844,
    fontScale = 2f,
)
@Composable
private fun SextouFilterSheetLargeFontPreview() {
    SextouTheme {
        SextouFilterSheet(
            title = stringResource(R.string.filter_sheet_preview_title),
            groups = filterSheetPreviewGroups(),
            selectedIds = setOf("type-skewer", "price-high", "kids-space"),
            applyLabel = stringResource(R.string.filter_sheet_preview_apply),
            closeContentDescription = stringResource(R.string.filter_sheet_preview_close),
            onSelectionChanged = { _, _ -> },
            onApply = {},
            onDismiss = {},
        )
    }
}
