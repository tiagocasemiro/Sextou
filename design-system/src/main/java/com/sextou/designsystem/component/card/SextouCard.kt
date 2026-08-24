package com.sextou.designsystem.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouShapes
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTheme

@Composable
fun SextouCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = SextouShapes.medium
    val colors = CardDefaults.cardColors(containerColor = SextouColors.Surface)
    val elevation = CardDefaults.cardElevation(defaultElevation = SextouDimensions.CardElevation)
    val border = BorderStroke(SextouDimensions.Border, SextouColors.Border)

    if (onClick == null) {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            border = border,
            elevation = elevation,
            content = content,
        )
    } else {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = colors,
            border = border,
            elevation = elevation,
            content = content,
        )
    }
}

@Preview(
    name = "Sextou card",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouCardPreview() {
    SextouTheme {
        SextouCard(modifier = Modifier.padding(SextouSpacing.Lg)) {
            Column(modifier = Modifier.padding(SextouSpacing.CardContent)) {
                Text(
                    text = stringResource(R.string.design_system_preview_card_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(R.string.design_system_preview_card_metadata),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SextouColors.TextSecondary,
                )
            }
        }
    }
}

@Preview(
    name = "Sextou clickable card",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouClickableCardPreview() {
    SextouTheme {
        SextouCard(
            modifier = Modifier.padding(SextouSpacing.Lg),
            onClick = {},
        ) {
            Column(modifier = Modifier.padding(SextouSpacing.CardContent)) {
                Text(
                    text = stringResource(R.string.design_system_preview_card_title),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}
