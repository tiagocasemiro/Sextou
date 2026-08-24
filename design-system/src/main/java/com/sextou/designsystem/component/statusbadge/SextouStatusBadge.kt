package com.sextou.designsystem.component.statusbadge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTheme

enum class SextouStatus {
    OPEN,
    CLOSED,
    UNAVAILABLE,
}

/**
 * Displays a compact, non-interactive status badge with a semantic indicator.
 *
 * @param status Semantic status that defines the label and visual style.
 * @param modifier Modifier applied to the badge container.
 * @param style Token-backed visual style for the selected [status].
 *
 * @see SextouStatusBadgeDefaults
 * @see SextouStatusBadgeDefaults.Style
 */
@Composable
fun SextouStatusBadge(
    status: SextouStatus,
    modifier: Modifier = Modifier,
    style: SextouStatusBadgeDefaults.Style = SextouStatusBadgeDefaults.styleFor(status),
) {
    val label = when (status) {
        SextouStatus.OPEN -> stringResource(R.string.design_system_status_open)
        SextouStatus.CLOSED -> stringResource(R.string.design_system_status_closed)
        SextouStatus.UNAVAILABLE -> stringResource(R.string.design_system_status_unavailable)
    }

    Row(
        modifier = modifier
            .height(SextouStatusBadgeDefaults.Height)
            .background(style.containerColor, SextouStatusBadgeDefaults.Shape)
            .padding(
                horizontal = SextouStatusBadgeDefaults.HorizontalPadding,
                vertical = SextouStatusBadgeDefaults.VerticalPadding,
            ),
        horizontalArrangement = Arrangement.spacedBy(SextouStatusBadgeDefaults.ContentGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(SextouStatusBadgeDefaults.IndicatorSize)
                .background(style.indicatorColor, CircleShape),
        )
        Text(
            text = label,
            style = SextouStatusBadgeDefaults.TextStyle,
            color = style.contentColor,
            maxLines = 1,
        )
    }
}

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
            SextouStatusBadge(status = SextouStatus.UNAVAILABLE)
        }
    }
}
