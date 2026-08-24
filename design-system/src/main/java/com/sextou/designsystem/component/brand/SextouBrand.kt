package com.sextou.designsystem.component.brand

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouDimensions
import com.sextou.designsystem.theme.SextouShapes
import com.sextou.designsystem.theme.SextouSpacing
import com.sextou.designsystem.theme.SextouTextStyles
import com.sextou.designsystem.theme.SextouTheme

@Composable
fun SextouBrand(
    iconPainter: Painter,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    iconContentDescription: String? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SextouSpacing.Xs),
    ) {
        Box(
            modifier = Modifier
                .size(SextouDimensions.BrandIcon)
                .clip(SextouShapes.small)
                .background(SextouColors.Primary),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = iconPainter,
                contentDescription = iconContentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(SextouDimensions.BrandTextGap),
        ) {
            Text(
                text = title,
                style = SextouTextStyles.Brand,
                color = SextouColors.PrimaryStrong,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = SextouTextStyles.BrandSubtitle,
                color = SextouColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(
    name = "Sextou brand",
    showBackground = true,
    backgroundColor = SextouColors.BackgroundArgb,
)
@Composable
private fun SextouBrandPreview() {
    SextouTheme {
        SextouBrand(
            iconPainter = painterResource(R.drawable.ic_sextou_chopp),
            title = stringResource(R.string.design_system_preview_brand_title),
            subtitle = stringResource(R.string.design_system_preview_brand_subtitle),
            iconContentDescription = stringResource(
                R.string.design_system_preview_brand_icon_content_description,
            ),
        )
    }
}
