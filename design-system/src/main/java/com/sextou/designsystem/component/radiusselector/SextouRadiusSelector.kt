package com.sextou.designsystem.component.radiusselector

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouTheme

/**
 * Modal single selection with an optional stepped custom value, controlled by the caller.
 *
 * @param title Dialog title.
 * @param options Labeled choices with stable IDs.
 * @param selectedOptionId Currently selected choice.
 * @param customOptionId Choice that displays the slider.
 * @param customValue Current slider value in caller-defined units.
 * @param customValueLabel Formatted value including its unit.
 * @param customValueDescription Accessible label for the slider.
 * @param valueRange Allowed slider range.
 * @param steps Number of intermediate discrete values, excluding endpoints.
 * @param confirmLabel Confirmation text.
 * @param cancelLabel Cancellation text.
 * @param onSelectionChanged Reports provisional selection.
 * @param onCustomValueChanged Reports provisional slider changes.
 * @param onConfirm Reports explicit confirmation.
 * @param onCancel Reports dismissal or cancellation.
 * @param modifier Applied to the dialog root.
 * @param enabled Enables selection, confirmation and dismissal.
 * @param errorText Optional feedback for a failed confirmation.
 * @param style Semantic visual tokens.
 * @see SextouRadiusSelectorDefaults.OptionData
 * @see SextouRadiusSelectorDefaults.Style
 */
@Composable
fun SextouRadiusSelector(
    title: String,
    options: List<SextouRadiusSelectorDefaults.OptionData>,
    selectedOptionId: Int,
    customOptionId: Int,
    customValue: Float,
    customValueLabel: String,
    customValueDescription: String,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    confirmLabel: String,
    cancelLabel: String,
    onSelectionChanged: (Int) -> Unit,
    onCustomValueChanged: (Float) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    errorText: String? = null,
    style: SextouRadiusSelectorDefaults.Style = SextouRadiusSelectorDefaults.defaultStyle(),
) {
    AlertDialog(
        onDismissRequest = { if (enabled) onCancel() },
        modifier = modifier,
        shape = style.shape,
        containerColor = style.containerColor,
        titleContentColor = style.contentColor,
        textContentColor = style.contentColor,
        title = { Text(title, style = style.titleTextStyle) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Column(Modifier.selectableGroup()) {
                    options.forEach { option ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .heightIn(min = SextouRadiusSelectorDefaults.optionHeight)
                                .selectable(
                                    selected = option.id == selectedOptionId,
                                    enabled = enabled,
                                    role = Role.RadioButton,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(color = style.rippleColor),
                                    onClick = { onSelectionChanged(option.id) },
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(SextouRadiusSelectorDefaults.optionGap),
                        ) {
                            RadioButton(selected = option.id == selectedOptionId, onClick = null, enabled = enabled)
                            Text(option.label, style = style.optionTextStyle)
                        }
                    }
                }
                errorText?.let { Text(it, color = style.errorColor, style = style.optionTextStyle) }
                if (selectedOptionId == customOptionId) {
                    Text(customValueLabel, style = style.optionTextStyle)
                    Slider(
                        value = customValue,
                        onValueChange = onCustomValueChanged,
                        valueRange = valueRange,
                        steps = steps,
                        enabled = enabled,
                        modifier = Modifier.semantics {
                            contentDescription = customValueDescription
                            stateDescription = customValueLabel
                        },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = enabled) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onCancel, enabled = enabled) { Text(cancelLabel) }
        },
    )
}

@Preview
@Composable
private fun SextouRadiusSelectorDefaultPreview() = SextouRadiusSelectorContentPreview(custom = false)

@Preview
@Composable
private fun SextouRadiusSelectorCustomPreview() = SextouRadiusSelectorContentPreview(custom = true)

@Preview
@Composable
private fun SextouRadiusSelectorDisabledPreview() = SextouRadiusSelectorContentPreview(custom = true, enabled = false)

@Composable
private fun SextouRadiusSelectorContentPreview(custom: Boolean, enabled: Boolean = true) {
    var selected by remember { mutableIntStateOf(if (custom) 0 else 1) }
    var value by remember { mutableFloatStateOf(3f) }
    SextouTheme {
        SextouRadiusSelector(
            title = stringResource(R.string.radius_preview_title),
            options = listOf(
                SextouRadiusSelectorDefaults.OptionData(1, stringResource(R.string.radius_preview_fixed)),
                SextouRadiusSelectorDefaults.OptionData(0, stringResource(R.string.radius_preview_custom)),
            ),
            selectedOptionId = selected,
            customOptionId = 0,
            customValue = value,
            customValueLabel = stringResource(R.string.radius_preview_value, value),
            customValueDescription = stringResource(R.string.radius_preview_title),
            valueRange = 0.5f..50f,
            steps = 98,
            confirmLabel = stringResource(R.string.radius_preview_confirm),
            cancelLabel = stringResource(R.string.radius_preview_cancel),
            onSelectionChanged = { selected = it },
            onCustomValueChanged = { value = it },
            onConfirm = {}, onCancel = {}, enabled = enabled,
        )
    }
}
