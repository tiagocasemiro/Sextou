package com.sextou.designsystem.component.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouTheme

/**
 * Displays a token-backed text input or search bar with hoisted text state.
 *
 * @param value Current text displayed by the field.
 * @param onValueChange Callback invoked when the user changes the text.
 * @param placeholder Guidance shown while [value] is empty.
 * @param modifier Modifier applied to the component root.
 * @param layout Visual layout to render. Use [SextouInputDefaults.Layout].
 * @param label Optional label displayed above the field.
 * @param supportingText Optional helper or validation message displayed below the field.
 * @param isError Whether the field is invalid and should show error styling.
 * @param enabled Whether the field and its optional action can be interacted with.
 * @param leadingIcon Optional leading icon. Search bars normally provide one.
 * @param leadingIconContentDescription Description for an informative leading icon; use null for decorative icons.
 * @param actionIcon Optional trailing action icon.
 * @param actionContentDescription Accessible description for the trailing action.
 * @param onActionClick Callback for the trailing action, when present.
 * @param interactionSource Hoisted source used to observe focus interactions.
 * @param style Token-backed visual style for the component.
 *
 * @see SextouInputDefaults
 * @see SextouInputDefaults.Layout
 * @see SextouInputDefaults.Style
 */
@Composable
fun SextouInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    layout: SextouInputDefaults.Layout = SextouInputDefaults.Layout.TextInput,
    label: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: Painter? = null,
    leadingIconContentDescription: String? = null,
    actionIcon: Painter? = null,
    actionContentDescription: String? = null,
    onActionClick: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    style: SextouInputDefaults.Style = SextouInputDefaults.defaultStyle(),
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    val containerColor = SextouInputDefaults.containerColor(isFocused, enabled, style)
    val borderColor = SextouInputDefaults.borderColor(isFocused, isError, enabled, style)
    val borderWidth = SextouInputDefaults.borderWidth(isFocused, enabled)
    val textColor = SextouInputDefaults.contentColor(enabled, style)
    val fieldShape = SextouInputDefaults.shape(layout, style)
    val fieldHeight = SextouInputDefaults.height(layout)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        label?.let {
            Text(
                text = it,
                modifier = Modifier.padding(start = SextouInputDefaults.LabelStartPadding),
                style = style.labelStyle,
                color = style.labelColor,
            )
            Spacer(modifier = Modifier.height(SextouInputDefaults.LabelFieldGap))
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(fieldHeight)
                .semantics {
                    label?.let { this.contentDescription = it }
                    if (isError && supportingText != null) {
                        error(supportingText)
                    }
                },
            enabled = enabled,
            singleLine = true,
            interactionSource = interactionSource,
            textStyle = style.textStyle.copy(color = textColor),
            cursorBrush = SolidColor(style.cursorColor),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(fieldShape)
                        .background(containerColor)
                        .border(borderWidth, borderColor, fieldShape)
                        .padding(horizontal = SextouInputDefaults.HorizontalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SextouInputDefaults.ElementGap),
                ) {
                    leadingIcon?.let { painter ->
                        Icon(
                            painter = painter,
                            contentDescription = leadingIconContentDescription,
                            modifier = Modifier.size(SextouInputDefaults.IconSize),
                            tint = if (enabled) {
                                if (isFocused) style.contentColor else style.leadingIconColor
                            } else {
                                style.disabledContentColor
                            },
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = style.textStyle,
                                color = if (enabled) style.placeholderColor else style.disabledContentColor,
                                maxLines = 1,
                            )
                        }
                        innerTextField()
                    }

                    actionIcon?.let { painter ->
                        InputAction(
                            painter = painter,
                            contentDescription = actionContentDescription,
                            enabled = enabled,
                            onClick = onActionClick,
                            style = style,
                        )
                    }
                }
            },
        )

        supportingText?.let {
            Spacer(modifier = Modifier.height(SextouInputDefaults.FieldSupportingGap))
            Text(
                text = it,
                modifier = Modifier.padding(start = SextouInputDefaults.SupportingStartPadding),
                style = if (isError) style.errorStyle else style.supportingStyle,
                color = if (isError) style.errorColor else style.supportingColor,
            )
        }
    }
}

@Composable
private fun InputAction(
    painter: Painter,
    contentDescription: String?,
    enabled: Boolean,
    onClick: (() -> Unit)?,
    style: SextouInputDefaults.Style,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val actionModifier = if (onClick != null) {
        Modifier
            .semantics {
                role = Role.Button
                contentDescription?.let { this.contentDescription = it }
            }
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple(color = style.rippleColor, bounded = true),
                onClick = { onClick() },
            )
    } else {
        Modifier
    }

    Box(
        modifier = actionModifier.size(SextouInputDefaults.ActionTouchTarget),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Box(
            modifier = Modifier
                .size(SextouInputDefaults.ActionButtonSize)
                .clip(style.actionShape)
                .background(if (enabled) style.actionColor else style.disabledActionColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(SextouDimensions.CompactIcon),
                tint = style.actionContentColor,
            )
        }
    }
}

@Preview(
    name = "Sextou input layouts",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouInputLayoutsPreview() {
    SextouTheme {
        Column(
            modifier = Modifier.padding(SextouInputDefaults.HorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(SextouInputDefaults.ElementGap),
        ) {
            SextouInput(
                value = "",
                onValueChange = {},
                placeholder = stringResource(R.string.design_system_preview_input_placeholder),
                label = stringResource(R.string.design_system_preview_input_label),
                supportingText = stringResource(R.string.design_system_preview_input_helper),
            )
            SextouInput(
                value = stringResource(R.string.design_system_preview_search_value),
                onValueChange = {},
                placeholder = stringResource(R.string.design_system_preview_search_placeholder),
                layout = SextouInputDefaults.Layout.SearchBar,
                leadingIcon = painterResource(R.drawable.ic_sextou_search),
                actionIcon = painterResource(R.drawable.ic_sextou_filter),
                actionContentDescription = stringResource(
                    R.string.design_system_preview_search_filter_content_description,
                ),
                onActionClick = {},
            )
        }
    }
}

@Preview(
    name = "Sextou input states",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouInputStatesPreview() {
    SextouTheme {
        Column(
            modifier = Modifier.padding(SextouInputDefaults.HorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(SextouInputDefaults.ElementGap),
        ) {
            SextouInput(
                value = stringResource(R.string.design_system_preview_input_value),
                onValueChange = {},
                placeholder = stringResource(R.string.design_system_preview_input_placeholder),
                label = stringResource(R.string.design_system_preview_input_label),
            )
            SextouInput(
                value = stringResource(R.string.design_system_preview_input_value),
                onValueChange = {},
                placeholder = stringResource(R.string.design_system_preview_input_placeholder),
                label = stringResource(R.string.design_system_preview_input_label),
                isError = true,
                supportingText = stringResource(R.string.design_system_preview_input_error),
            )
            SextouInput(
                value = "",
                onValueChange = {},
                placeholder = stringResource(R.string.design_system_preview_input_placeholder),
                enabled = false,
            )
        }
    }
}
