package com.sextou.designsystem.component.quickaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouPrimitiveAlpha
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTheme

/**
 * Displays a compact quick-action tile for an establishment.
 * The action selects the fixed icon and uppercase label shown in the tile.
 * Clicking the tile emits [onClick]; the selected icon variant is controlled
 * by [selected], layering the selected fill below the original outline.
 *
 * @param action Semantic action represented by the tile.
 * @param onClick Callback invoked when the tile is activated.
 * @param modifier Modifier applied to the tile container.
 * @param selected Whether the tile displays the selected fill below the
 * original icon outline.
 * @param enabled Whether the tile can be activated.
 * @param style Token-backed visual style for the tile.
 *
 * @see SextouQuickActionDefaults
 * @see SextouQuickActionDefaults.Action
 * @see SextouQuickActionDefaults.Style
 */
@Composable
fun SextouQuickAction(
    action: SextouQuickActionDefaults.Action,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    style: SextouQuickActionDefaults.Style = SextouQuickActionDefaults.defaultStyle(),
) {
    val iconResource = when (action) {
        SextouQuickActionDefaults.Action.FAVORITAR ->
            R.drawable.ic_sextou_quick_action_favoritar
        SextouQuickActionDefaults.Action.VISITAR ->
            R.drawable.ic_sextou_quick_action_visitar
        SextouQuickActionDefaults.Action.IGNORAR ->
            R.drawable.ic_sextou_quick_action_ignorar
    }
    val fillResource = if (selected) {
        when (action) {
            SextouQuickActionDefaults.Action.FAVORITAR ->
                R.drawable.ic_sextou_quick_action_favoritar_filled
            SextouQuickActionDefaults.Action.VISITAR ->
                R.drawable.ic_sextou_quick_action_visitar_filled
            SextouQuickActionDefaults.Action.IGNORAR ->
                R.drawable.ic_sextou_quick_action_ignorar_fill
        }
    } else {
        null
    }
    val labelResource = when (action) {
        SextouQuickActionDefaults.Action.FAVORITAR -> R.string.design_system_quick_action_favoritar
        SextouQuickActionDefaults.Action.VISITAR -> R.string.design_system_quick_action_visitar
        SextouQuickActionDefaults.Action.IGNORAR -> R.string.design_system_quick_action_ignorar
    }
    val iconColor = when (action) {
        SextouQuickActionDefaults.Action.IGNORAR -> style.mutedIconColor
        SextouQuickActionDefaults.Action.FAVORITAR,
        SextouQuickActionDefaults.Action.VISITAR -> style.emphasisIconColor
    }
    val selectedFillColor = if (selected) {
        iconColor.copy(alpha = SextouPrimitiveAlpha.QuickActionSelectedIconAlpha)
    } else {
        iconColor
    }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .size(
                width = SextouQuickActionDefaults.Width,
                height = SextouQuickActionDefaults.Height,
            )
            .clip(style.shape)
            .background(style.containerColor, style.shape)
            .border(
                BorderStroke(style.borderWidth, style.borderColor),
                style.shape,
            )
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = rememberRipple(color = style.rippleColor, bounded = true),
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .focusable(enabled)
            .padding(
                top = style.verticalPadding + style.borderWidth,
                bottom = style.verticalPadding + style.borderWidth,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(style.iconLabelSpacing),
    ) {
        Box(
            modifier = Modifier
                .height(SextouQuickActionDefaults.IconContainerHeight)
                .padding(
                    top = SextouQuickActionDefaults.IconPaddingTop,
                    bottom = SextouQuickActionDefaults.IconPaddingBottom,
                ),
            contentAlignment = Alignment.Center,
        ) {
            fillResource?.let { resource ->
                Icon(
                    painter = painterResource(resource),
                    contentDescription = null,
                    modifier = Modifier.size(
                        width = SextouQuickActionDefaults.iconWidth(action),
                        height = SextouQuickActionDefaults.IconSize,
                    ),
                    tint = selectedFillColor,
                )
            }
            Icon(
                painter = painterResource(iconResource),
                contentDescription = null,
                modifier = Modifier.size(
                    width = SextouQuickActionDefaults.iconWidth(action),
                    height = SextouQuickActionDefaults.IconSize,
                ),
                tint = iconColor,
            )
        }
        Text(
            text = stringResource(labelResource),
            style = style.labelTextStyle,
            color = style.labelColor,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Preview(
    name = "Sextou quick actions",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouQuickActionPreview() {
    SextouTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Sm)) {
            SextouQuickAction(
                action = SextouQuickActionDefaults.Action.FAVORITAR,
                onClick = {},
            )
            SextouQuickAction(
                action = SextouQuickActionDefaults.Action.VISITAR,
                onClick = {},
            )
            SextouQuickAction(
                action = SextouQuickActionDefaults.Action.IGNORAR,
                onClick = {},
            )
        }
    }
}

@Preview(
    name = "Sextou quick actions selected",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouQuickActionSelectedPreview() {
    SextouTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Sm)) {
            SextouQuickAction(
                action = SextouQuickActionDefaults.Action.FAVORITAR,
                onClick = {},
                selected = true,
            )
            SextouQuickAction(
                action = SextouQuickActionDefaults.Action.VISITAR,
                onClick = {},
                selected = true,
            )
            SextouQuickAction(
                action = SextouQuickActionDefaults.Action.IGNORAR,
                onClick = {},
                selected = true,
            )
        }
    }
}
