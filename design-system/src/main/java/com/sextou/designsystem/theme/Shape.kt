package com.sextou.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object SextouCornerRadius {
    val Control = 10.dp
    val Chip = 14.dp
    val Surface = 16.dp
    val AppShell = 44.dp
}

val SextouShapes = Shapes(
    extraSmall = RoundedCornerShape(SextouCornerRadius.Control),
    small = RoundedCornerShape(SextouCornerRadius.Chip),
    medium = RoundedCornerShape(SextouCornerRadius.Surface),
    large = RoundedCornerShape(SextouCornerRadius.Surface),
    extraLarge = RoundedCornerShape(SextouCornerRadius.AppShell),
)
