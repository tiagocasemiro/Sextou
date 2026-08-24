package com.sextou.designsystem.component.searchbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.component.input.SextouInput
import com.sextou.designsystem.component.input.SextouInputDefaults
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTheme

/**
 * Displays the legacy Search Bar API backed by [SextouInput].
 *
 * @param value Current search query.
 * @param onValueChange Callback invoked when the query changes.
 * @param placeholder Guidance shown while [value] is empty.
 * @param onFilterClick Callback invoked by the filter action.
 * @param filterContentDescription Accessible description for the filter action.
 * @param modifier Modifier applied to the search bar.
 * @param enabled Whether the search field and filter action can be interacted with.
 */
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
    SextouInput(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        modifier = modifier.fillMaxWidth(),
        layout = SextouInputDefaults.Layout.SearchBar,
        enabled = enabled,
        leadingIcon = painterResource(R.drawable.ic_sextou_search),
        actionIcon = painterResource(R.drawable.ic_sextou_filter),
        actionContentDescription = filterContentDescription,
        onActionClick = onFilterClick,
    )
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
