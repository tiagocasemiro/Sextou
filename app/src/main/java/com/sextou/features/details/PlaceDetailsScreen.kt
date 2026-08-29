package com.sextou.features.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sextou.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PlaceDetailsScreen(
    uiState: PlaceDetailsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.details_title)) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = stringResource(R.string.details_back))
                    }
                },
            )
        },
        containerColor = SextouColors.Background,
    ) { contentPadding ->
        when {
            uiState.isLoading && uiState.place == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(color = SextouColors.Primary)
                }
            }

            uiState.place == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
                ) {
                    Text(
                        text = stringResource(R.string.details_error),
                        style = SextouTextStyles.BodyLarge,
                        color = SextouColors.TextSecondary,
                    )
                    TextButton(onClick = onBack) {
                        Text(text = stringResource(R.string.details_back))
                    }
                }
            }

            else -> PlaceDetailsContent(
                place = uiState.place,
                contentPadding = contentPadding,
            )
        }
    }
}

@Composable
private fun PlaceDetailsContent(
    place: PlaceDetailsUiModel?,
    contentPadding: PaddingValues,
) {
    val details = place ?: return
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
    ) {
        item {
            Text(
                text = details.name,
                style = SextouTextStyles.HeadlineMedium,
                color = SextouColors.TextPrimary,
            )
        }
        details.summary?.let { summary ->
            item {
                Text(
                    text = summary,
                    style = SextouTextStyles.BodyLarge,
                    color = SextouColors.TextSecondary,
                )
            }
        }
        details.address?.let { address ->
            item { DetailLine(label = stringResource(R.string.details_address), value = address) }
        }
        details.phone?.let { phone ->
            item { DetailLine(label = stringResource(R.string.details_phone), value = phone) }
        }
        details.website?.let { website ->
            item { DetailLine(label = stringResource(R.string.details_website), value = website) }
        }
        if (details.rating != null || details.ratingsCount != null) {
            item {
                val rating = details.rating?.let {
                    stringResource(R.string.details_rating_value, it)
                }
                val count = details.ratingsCount?.let {
                    pluralStringResource(R.plurals.details_rating_count, it, it)
                }
                DetailLine(
                    label = stringResource(R.string.details_rating),
                    value = listOfNotNull(rating, count).joinToString(" "),
                )
            }
        }
        if (details.hours.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(SextouSpacing.Xs)) {
                    Text(
                        text = stringResource(R.string.details_hours),
                        style = SextouTextStyles.Category,
                        color = SextouColors.TextSecondary,
                    )
                    details.hours.forEach { hours ->
                        Text(
                            text = hours,
                            style = SextouTextStyles.BodyLarge,
                            color = SextouColors.TextPrimary,
                        )
                    }
                }
            }
        }
        item {
            Text(
                text = stringResource(
                    R.string.details_provider_attribution,
                    details.providerAttribution,
                ),
                style = SextouTextStyles.Metadata,
                color = SextouColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun DetailLine(
    label: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SextouSpacing.Xs)) {
        Text(
            text = label,
            style = SextouTextStyles.Category,
            color = SextouColors.TextSecondary,
        )
        Text(
            text = value,
            style = SextouTextStyles.BodyLarge,
            color = SextouColors.TextPrimary,
        )
    }
}
