package com.sextou.designsystem.component.morebutton

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouShapes
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.theme.SextouTheme

@Composable
fun SextouMoreButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = SextouShapes.medium,
        border = BorderStroke(SextouDimensions.Border, SextouColors.Border),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = SextouColors.TextSecondary,
            disabledContentColor = SextouColors.TextMuted,
        ),
        contentPadding = PaddingValues(
            horizontal = SextouDimensions.MoreButtonHorizontalPadding,
            vertical = SextouDimensions.MoreButtonVerticalPadding,
        ),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_sextou_beer),
            contentDescription = null,
            modifier = Modifier.size(SextouDimensions.MoreButtonIcon),
            tint = if (enabled) SextouColors.Primary else SextouColors.TextMuted,
        )
        Text(
            text = text,
            style = SextouTextStyles.ActionButton,
            color = if (enabled) SextouColors.TextSecondary else SextouColors.TextMuted,
            maxLines = 1,
        )
    }
}

@Preview(
    name = "Sextou more button",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouMoreButtonPreview() {
    SextouTheme {
        SextouMoreButton(
            text = stringResource(R.string.design_system_preview_more_button),
            onClick = {},
        )
    }
}

@Preview(
    name = "Sextou more button states",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouMoreButtonStatesPreview() {
    SextouTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
        ) {
            SextouMoreButton(
                text = stringResource(R.string.design_system_preview_more_button),
                onClick = {},
            )
            SextouMoreButton(
                text = stringResource(R.string.design_system_preview_more_button_variable),
                onClick = {},
                enabled = false,
            )
        }
    }
}
