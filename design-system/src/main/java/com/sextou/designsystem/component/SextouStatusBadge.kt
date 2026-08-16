package com.sextou.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouShapes
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.theme.SextouTheme

enum class SextouStatus {
    OPEN,
    CLOSED,
}

@Composable
fun SextouStatusBadge(
    status: SextouStatus,
    modifier: Modifier = Modifier,
) {
    val style = when (status) {
        SextouStatus.OPEN -> StatusBadgeStyle(
            containerColor = SextouColors.StatusOpenContainer,
            indicatorColor = SextouColors.StatusOpenIndicator,
            contentColor = SextouColors.StatusOpenContent,
            label = stringResource(R.string.design_system_status_open),
        )

        SextouStatus.CLOSED -> StatusBadgeStyle(
            containerColor = SextouColors.StatusClosedContainer,
            indicatorColor = SextouColors.StatusClosedIndicator,
            contentColor = SextouColors.StatusClosedContent,
            label = stringResource(R.string.design_system_status_closed),
        )
    }

    Row(
        modifier = modifier
            .background(style.containerColor, SextouShapes.small)
            .padding(
                horizontal = SextouDimensions.StatusBadgeHorizontalPadding,
                vertical = SextouDimensions.StatusBadgeVerticalPadding,
            ),
        horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(SextouDimensions.StatusBadgeIndicator)
                .background(style.indicatorColor, CircleShape),
        )
        Text(
            text = style.label,
            style = SextouTextStyles.Status,
            color = style.contentColor,
            maxLines = 1,
        )
    }
}

private data class StatusBadgeStyle(
    val containerColor: Color,
    val indicatorColor: Color,
    val contentColor: Color,
    val label: String,
)

@Preview(
    name = "Sextou status badges",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouStatusBadgePreview() {
    SextouTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Md)) {
            SextouStatusBadge(status = SextouStatus.OPEN)
            SextouStatusBadge(status = SextouStatus.CLOSED)
        }
    }
}
