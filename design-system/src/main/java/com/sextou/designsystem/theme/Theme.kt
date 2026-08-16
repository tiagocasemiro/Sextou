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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.component.SextouButton
import com.sextou.designsystem.component.SextouCard
import com.sextou.designsystem.component.SextouOutlinedButton

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
    primaryContainer = ColorTokens.LightPrimaryContainer,
    onPrimaryContainer = ColorTokens.LightOnPrimaryContainer,
    secondary = SextouColors.PrimaryStrong,
    onSecondary = SextouColors.OnPrimary,
    secondaryContainer = ColorTokens.LightSecondaryContainer,
    onSecondaryContainer = ColorTokens.LightOnSecondaryContainer,
    tertiary = SextouColors.PositiveStrong,
    onTertiary = SextouColors.OnPrimary,
    tertiaryContainer = ColorTokens.LightTertiaryContainer,
    onTertiaryContainer = ColorTokens.LightOnTertiaryContainer,
    background = ColorTokens.LightBackground,
    onBackground = ColorTokens.LightOnBackground,
    surface = ColorTokens.LightSurface,
    onSurface = ColorTokens.LightOnSurface,
    surfaceVariant = ColorTokens.LightSurfaceVariant,
    onSurfaceVariant = ColorTokens.LightOnSurfaceVariant,
    outline = ColorTokens.LightOutline,
    outlineVariant = ColorTokens.LightOutlineVariant,
    error = SextouColors.Error,
    onError = ColorTokens.LightOnError,
)

private object ColorTokens {
    val LightPrimaryContainer = Color(0xFFFFE0B2)
    val LightOnPrimaryContainer = Color(0xFF2B1700)
    val LightSecondaryContainer = Color(0xFFFFE7A8)
    val LightOnSecondaryContainer = Color(0xFF241A00)
    val LightTertiaryContainer = Color(0xFFB9F4D4)
    val LightOnTertiaryContainer = Color(0xFF002113)
    val LightBackground = Color(0xFFFFF9F3)
    val LightOnBackground = Color(0xFF211A14)
    val LightSurface = Color(0xFFFFF9F3)
    val LightOnSurface = Color(0xFF211A14)
    val LightSurfaceVariant = Color(0xFFF1E5D8)
    val LightOnSurfaceVariant = Color(0xFF625B52)
    val LightOutline = Color(0xFF85746A)
    val LightOutlineVariant = Color(0xFFDAC9BB)
    val LightOnError = Color(0xFFFFFFFF)
}

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
    backgroundColor = 0xFF111111,
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
