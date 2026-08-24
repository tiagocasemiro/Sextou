package com.sextou.designsystem.component.iconbutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouShapes
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTheme

@Composable
fun SextouIconButton(
    iconPainter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Surface(
        modifier = modifier.size(SextouDimensions.CompactIconButtonTouchTarget),
        onClick = onClick,
        enabled = enabled,
        color = Color.Transparent,
        contentColor = SextouColors.OnPrimary,
    ) {
        Surface(
            modifier = Modifier
                .size(SextouDimensions.CompactIconButton),
            shape = SextouShapes.extraSmall,
            color = SextouColors.Primary,
            contentColor = SextouColors.OnPrimary,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = iconPainter,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(SextouDimensions.CompactIcon),
                    tint = SextouColors.OnPrimary,
                )
            }
        }
    }
}

@Preview(
    name = "Sextou icon button",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouIconButtonPreview() {
    SextouTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Md)) {
            SextouIconButton(
                iconPainter = painterResource(R.drawable.ic_sextou_filter),
                contentDescription = stringResource(
                    R.string.design_system_preview_icon_button_content_description,
                ),
                onClick = {},
            )
            SextouIconButton(
                iconPainter = painterResource(R.drawable.ic_sextou_filter),
                contentDescription = stringResource(
                    R.string.design_system_preview_icon_button_content_description,
                ),
                onClick = {},
                enabled = false,
            )
        }
    }
}
