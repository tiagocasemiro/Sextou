package com.sextou.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouShapes
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.theme.SextouTheme

@Composable
fun SextouSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onFilterClick: () -> Unit,
    filterContentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SextouColors.SurfaceElevated, SextouShapes.medium)
            .border(
                width = SextouDimensions.Border,
                color = SextouColors.Border,
                shape = SextouShapes.medium,
            )
            .padding(
                horizontal = SextouSpacing.Lg,
                vertical = SextouDimensions.SearchBarVerticalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_sextou_search),
            contentDescription = null,
            modifier = Modifier.size(SextouDimensions.SearchIcon),
            tint = Color.Unspecified,
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .height(SextouDimensions.SearchInputHeight),
            enabled = enabled,
            singleLine = true,
            textStyle = SextouTextStyles.Search.copy(color = SextouColors.TextPrimary),
            cursorBrush = SolidColor(SextouColors.Primary),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = SextouTextStyles.Search,
                        color = if (enabled) {
                            SextouColors.TextSecondary
                        } else {
                            SextouColors.TextMuted
                        },
                        maxLines = 1,
                    )
                }
                innerTextField()
            },
        )

        Box(
            modifier = Modifier
                .size(SextouDimensions.SearchFilterTouchTarget)
                .semantics {
                    this.contentDescription = filterContentDescription
                    role = Role.Button
                }
                .clickable(
                    enabled = enabled,
                    role = Role.Button,
                    onClick = onFilterClick,
                ),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                modifier = Modifier
                    .size(SextouDimensions.CompactIconButton)
                    .clip(SextouShapes.extraSmall)
                    .background(SextouColors.Primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sextou_filter),
                    contentDescription = null,
                    modifier = Modifier.size(SextouDimensions.CompactIcon),
                    tint = Color.Unspecified,
                )
            }
        }
    }
}

@Preview(
    name = "Sextou search bar",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouSearchBarPreview() {
    SextouTheme {
        SextouSearchBar(
            value = "",
            onValueChange = {},
            placeholder = stringResource(R.string.design_system_preview_search_placeholder),
            onFilterClick = {},
            filterContentDescription = stringResource(
                R.string.design_system_preview_search_filter_content_description,
            ),
            modifier = Modifier.padding(SextouSpacing.Lg),
        )
    }
}

@Preview(
    name = "Sextou search bar states",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouSearchBarStatesPreview() {
    SextouTheme {
        Row(
            modifier = Modifier.padding(SextouSpacing.Lg),
            horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Lg),
        ) {
            SextouSearchBar(
                value = stringResource(R.string.design_system_preview_search_value),
                onValueChange = {},
                placeholder = stringResource(R.string.design_system_preview_search_placeholder),
                onFilterClick = {},
                filterContentDescription = stringResource(
                    R.string.design_system_preview_search_filter_content_description,
                ),
                modifier = Modifier.weight(1f),
            )
            SextouSearchBar(
                value = "",
                onValueChange = {},
                placeholder = stringResource(R.string.design_system_preview_search_placeholder),
                onFilterClick = {},
                filterContentDescription = stringResource(
                    R.string.design_system_preview_search_filter_content_description,
                ),
                modifier = Modifier.weight(1f),
                enabled = false,
            )
        }
    }
}
