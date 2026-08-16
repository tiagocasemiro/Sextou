package com.sextou.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sextou.designsystem.R
import com.sextou.designsystem.theme.SextouColors
import com.sextou.designsystem.theme.SextouTheme

@Composable
fun SextouProfileButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showBadge: Boolean = true,
    avatarPainter: Painter? = null,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .background(SextouColors.SurfaceImage, CircleShape)
            .border(1.dp, SextouColors.Border, CircleShape)
            .semantics { this.contentDescription = contentDescription }
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (avatarPainter == null) {
                Icon(
                    painter = painterResource(R.drawable.ic_sextou_profile),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Unspecified,
                )
            } else {
                Image(
                    painter = avatarPainter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        if (showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 1.dp, y = (-1).dp)
                    .size(14.dp)
                    .background(SextouColors.Error, CircleShape)
                    .border(2.dp, SextouColors.Background, CircleShape),
            )
        }
    }
}

@Preview(
    name = "Sextou profile button",
    showBackground = true,
    backgroundColor = 0xFF111111,
)
@Composable
private fun SextouProfileButtonPreview() {
    SextouTheme {
        SextouProfileButton(
            contentDescription = stringResource(R.string.design_system_preview_profile_button_content_description),
            onClick = {},
        )
    }
}
