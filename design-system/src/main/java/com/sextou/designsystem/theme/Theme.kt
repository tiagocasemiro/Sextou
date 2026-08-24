package com.sextou.designsystem.theme

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.component.button.SextouButton
import com.sextou.designsystem.component.button.SextouOutlinedButton
import com.sextou.designsystem.component.card.SextouCard

private val SextouDarkColorScheme = darkColorScheme(
    primary = SextouColors.Primary,
    onPrimary = SextouColors.OnPrimary,
    primaryContainer = SextouColors.SurfaceElevated,
    onPrimaryContainer = SextouColors.TextPrimary,
    secondary = SextouColors.PrimaryStrong,
    onSecondary = SextouColors.OnPrimary,
    secondaryContainer = SextouColors.Surface,
    onSecondaryContainer = SextouColors.TextPrimary,
    tertiary = SextouColors.Positive,
    onTertiary = SextouColors.OnPrimary,
    tertiaryContainer = SextouColors.PositiveStrong,
    onTertiaryContainer = SextouColors.OnPrimary,
    background = SextouColors.Background,
    onBackground = SextouColors.TextPrimary,
    surface = SextouColors.Background,
    onSurface = SextouColors.TextPrimary,
    surfaceVariant = SextouColors.Surface,
    onSurfaceVariant = SextouColors.TextSecondary,
    outline = SextouColors.Border,
    outlineVariant = SextouColors.Border,
    error = SextouColors.Error,
    onError = SextouColors.TextPrimary,
)

private val SextouLightColorScheme = lightColorScheme(
    primary = SextouColors.Primary,
    onPrimary = SextouColors.OnPrimary,
    primaryContainer = SextouLightColors.PrimaryContainer,
    onPrimaryContainer = SextouLightColors.OnPrimaryContainer,
    secondary = SextouColors.PrimaryStrong,
    onSecondary = SextouColors.OnPrimary,
    secondaryContainer = SextouLightColors.SecondaryContainer,
    onSecondaryContainer = SextouLightColors.OnSecondaryContainer,
    tertiary = SextouColors.PositiveStrong,
    onTertiary = SextouColors.OnPrimary,
    tertiaryContainer = SextouLightColors.TertiaryContainer,
    onTertiaryContainer = SextouLightColors.OnTertiaryContainer,
    background = SextouLightColors.Background,
    onBackground = SextouLightColors.OnBackground,
    surface = SextouLightColors.Surface,
    onSurface = SextouLightColors.OnSurface,
    surfaceVariant = SextouLightColors.SurfaceVariant,
    onSurfaceVariant = SextouLightColors.OnSurfaceVariant,
    outline = SextouLightColors.Outline,
    outlineVariant = SextouLightColors.OutlineVariant,
    error = SextouColors.Error,
    onError = SextouLightColors.OnError,
)

@Composable
fun SextouTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> SextouDarkColorScheme
        else -> SextouLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SextouTypography,
        shapes = SextouShapes,
        content = content,
    )
}

@Preview(
    name = "Sextou theme",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouThemePreview() {
    SextouTheme {
        Column(
            modifier = Modifier.padding(SextouSpacing.Xl),
            verticalArrangement = Arrangement.spacedBy(SextouSpacing.Md),
        ) {
            Text(
                text = stringResource(R.string.design_system_preview_theme_title),
                style = SextouTextStyles.Brand,
                color = SextouColors.PrimaryStrong,
            )
            Text(
                text = stringResource(R.string.design_system_preview_theme_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = SextouColors.TextSecondary,
            )
            Spacer(modifier = Modifier.height(SextouSpacing.Xs))
            SextouButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
            ) {
                Text(text = stringResource(R.string.design_system_preview_primary_button))
            }
            SextouOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {},
            ) {
                Text(text = stringResource(R.string.design_system_preview_outlined_button))
            }
            SextouCard(modifier = Modifier.fillMaxWidth()) {
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
}
