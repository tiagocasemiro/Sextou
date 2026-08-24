package com.sextou.designsystem.component.sectionheader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.theme.SextouTheme

@Composable
fun SextouSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
    ) {
        SectionHeaderDivider(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
        ) {
            SectionHeaderFlame()
            Text(
                text = text,
                style = SextouTextStyles.SectionTitle,
                color = SextouColors.TextPrimary,
                maxLines = 1,
            )
            SectionHeaderFlame()
        }

        SectionHeaderDivider(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SectionHeaderDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(SextouDimensions.SectionDividerThickness)
            .background(SextouColors.Divider),
    )
}

@Composable
private fun SectionHeaderFlame() {
    Icon(
        painter = painterResource(R.drawable.ic_sextou_flame),
        contentDescription = null,
        modifier = Modifier.size(SextouDimensions.SectionHeaderIcon),
        tint = Color.Unspecified,
    )
}

@Preview(
    name = "Sextou section header",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouSectionHeaderPreview() {
    SextouTheme {
        SextouSectionHeader(
            text = stringResource(R.string.design_system_preview_section_header),
        )
    }
}

@Preview(
    name = "Sextou section header with variable text",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouSectionHeaderVariableTextPreview() {
    SextouTheme {
        SextouSectionHeader(
            text = stringResource(R.string.design_system_preview_section_header_variable),
        )
    }
}
