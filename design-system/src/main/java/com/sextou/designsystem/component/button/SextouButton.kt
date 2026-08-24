package com.sextou.designsystem.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouShapes
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTheme

@Composable
fun SextouButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = SextouShapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = SextouColors.Primary,
            contentColor = SextouColors.OnPrimary,
        ),
        contentPadding = PaddingValues(
            horizontal = SextouSpacing.Lg,
            vertical = SextouSpacing.Md,
        ),
        content = content,
    )
}

@Composable
fun SextouOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = SextouShapes.medium,
        contentPadding = PaddingValues(
            horizontal = SextouSpacing.Lg,
            vertical = SextouSpacing.Md,
        ),
        content = content,
    )
}

@Preview(
    name = "Sextou button",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouButtonPreview() {
    SextouTheme {
        SextouButton(onClick = {}) {
            Text(text = stringResource(R.string.design_system_preview_primary_button))
        }
    }
}

@Preview(
    name = "Sextou outlined button",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouOutlinedButtonPreview() {
    SextouTheme {
        SextouOutlinedButton(onClick = {}) {
            Text(text = stringResource(R.string.design_system_preview_outlined_button))
        }
    }
}
