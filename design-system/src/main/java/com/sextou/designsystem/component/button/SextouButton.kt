package com.sextou.designsystem.component.button

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTheme

/**
 * Displays a token-backed action button with configurable size, style and icons.
 *
 * @param label Text shown as the button action.
 * @param onClick Callback invoked when the button is activated.
 * @param modifier Modifier applied to the button container.
 * @param size Visual size controlling height, padding, radius and typography.
 * @param leadingIcon Optional icon displayed before [label].
 * @param leadingIconContentDescription Accessible description for [leadingIcon], or null when decorative.
 * @param trailingIcon Optional icon displayed after [label].
 * @param trailingIconContentDescription Accessible description for [trailingIcon], or null when decorative.
 * @param enabled Whether the button can be activated.
 * @param style Token-backed visual style for the button variant.
 *
 * @see SextouButtonDefaults
 * @see SextouButtonDefaults.Size
 * @see SextouButtonDefaults.Style
 */
@Composable
fun SextouButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: SextouButtonDefaults.Size = SextouButtonDefaults.Size.Medium,
    leadingIcon: Painter? = null,
    leadingIconContentDescription: String? = null,
    trailingIcon: Painter? = null,
    trailingIconContentDescription: String? = null,
    enabled: Boolean = true,
    style: SextouButtonDefaults.Style = SextouButtonDefaults.primaryStyle(),
) {
    SextouButtonSurface(
        onClick = onClick,
        modifier = modifier,
        size = size,
        enabled = enabled,
        style = style,
    ) {
        leadingIcon?.let { painter ->
            Icon(
                painter = painter,
                contentDescription = leadingIconContentDescription,
                modifier = Modifier.size(SextouButtonDefaults.IconSize),
            )
        }
        Text(
            text = label,
            maxLines = 1,
        )
        trailingIcon?.let { painter ->
            Icon(
                painter = painter,
                contentDescription = trailingIconContentDescription,
                modifier = Modifier.size(SextouButtonDefaults.IconSize),
            )
        }
    }
}

/**
 * Displays a button with custom slot content while preserving the legacy Sextou API.
 *
 * @param onClick Callback invoked when the button is activated.
 * @param modifier Modifier applied to the button container.
 * @param enabled Whether the button can be activated.
 * @param size Visual size controlling height, padding, radius and typography.
 * @param style Token-backed visual style for the button variant.
 * @param content Content displayed inside the button.
 */
@Composable
fun SextouButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: SextouButtonDefaults.Size = SextouButtonDefaults.Size.Medium,
    style: SextouButtonDefaults.Style = SextouButtonDefaults.primaryStyle(),
    content: @Composable RowScope.() -> Unit,
) {
    SextouButtonSurface(
        onClick = onClick,
        modifier = modifier,
        size = size,
        enabled = enabled,
        style = style,
        content = content,
    )
}

/**
 * Displays an outline button using the shared button layout and tokens.
 *
 * @param onClick Callback invoked when the button is activated.
 * @param modifier Modifier applied to the button container.
 * @param enabled Whether the button can be activated.
 * @param size Visual size controlling height, padding, radius and typography.
 * @param content Content displayed inside the button.
 */
@Composable
fun SextouOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: SextouButtonDefaults.Size = SextouButtonDefaults.Size.Medium,
    content: @Composable RowScope.() -> Unit,
) {
    SextouButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        size = size,
        style = SextouButtonDefaults.outlineStyle(),
        content = content,
    )
}

@Composable
private fun SextouButtonSurface(
    onClick: () -> Unit,
    modifier: Modifier,
    size: SextouButtonDefaults.Size,
    enabled: Boolean,
    style: SextouButtonDefaults.Style,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isInteracting = enabled && (isHovered || isPressed)
    val scale by animateFloatAsState(
        targetValue = if (isInteracting) {
            SextouButtonDefaults.InteractionScale
        } else {
            SextouButtonDefaults.RestingScale
        },
        animationSpec = tween(
            durationMillis = SextouButtonDefaults.InteractionDurationMillis,
            easing = SextouButtonDefaults.InteractionEasing,
        ),
    )
    val shape = SextouButtonDefaults.shape(size)
    val containerColor = when {
        !enabled -> style.disabledContainerColor
        isInteracting -> style.interactionContainerColor
        else -> style.containerColor
    }
    val contentColor = when {
        !enabled -> style.disabledContentColor
        isInteracting -> style.interactionContentColor
        else -> style.contentColor
    }
    val borderColor = when {
        !enabled -> style.disabledBorderColor
        isInteracting -> style.interactionBorderColor
        else -> style.borderColor
    }

    Box(
        modifier = modifier
            .height(SextouButtonDefaults.height(size))
            .clip(shape)
            .background(containerColor)
            .then(
                if (style.borderWidth > SextouButtonDefaults.NoBorder) {
                    Modifier.border(style.borderWidth, borderColor, shape)
                } else {
                    Modifier
                },
            )
            .clickable(
                enabled = enabled,
                role = Role.Button,
                interactionSource = interactionSource,
                indication = rememberRipple(color = style.rippleColor, bounded = true),
                onClick = onClick,
            )
            .focusable(enabled)
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .padding(horizontal = SextouButtonDefaults.horizontalPadding(size)),
            horizontalArrangement = Arrangement.spacedBy(SextouButtonDefaults.IconGap),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides contentColor,
                    androidx.compose.material3.LocalTextStyle provides SextouButtonDefaults.textStyle(size),
                    content = { content() },
                )
            },
        )
    }
}

@Preview(
    name = "Sextou button variants",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouButtonVariantsPreview() {
    SextouTheme {
        Column(
            modifier = Modifier.padding(SextouSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
        ) {
            SextouButton(
                label = stringResource(R.string.design_system_preview_primary_button),
                onClick = {},
                leadingIcon = painterResource(R.drawable.ic_sextou_search),
                trailingIcon = painterResource(R.drawable.ic_sextou_filter),
            )
            SextouButton(
                label = stringResource(R.string.design_system_preview_secondary_button),
                onClick = {},
                style = SextouButtonDefaults.secondaryStyle(),
            )
            SextouButton(
                label = stringResource(R.string.design_system_preview_outlined_button),
                onClick = {},
                style = SextouButtonDefaults.outlineStyle(),
            )
            SextouButton(
                label = stringResource(R.string.design_system_preview_ghost_button),
                onClick = {},
                style = SextouButtonDefaults.ghostStyle(),
            )
        }
    }
}

@Preview(
    name = "Sextou button sizes and states",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouButtonSizesPreview() {
    SextouTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SextouSpacing.Lg),
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SextouButton(
                label = stringResource(R.string.design_system_preview_button_large),
                onClick = {},
                size = SextouButtonDefaults.Size.Large,
            )
            SextouButton(
                label = stringResource(R.string.design_system_preview_button_medium),
                onClick = {},
                size = SextouButtonDefaults.Size.Medium,
            )
            SextouButton(
                label = stringResource(R.string.design_system_preview_button_small),
                onClick = {},
                size = SextouButtonDefaults.Size.Small,
                enabled = false,
            )
        }
    }
}
