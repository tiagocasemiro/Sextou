package com.sextou.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Semantic corner-radius roles from the Figma semantic sheet (node 19:785). */
object SextouCornerRadius {
    val None = SextouPrimitiveCornerRadius.None
    val Small = SextouPrimitiveCornerRadius.Small
    val Base = SextouPrimitiveCornerRadius.Base
    val Medium = SextouPrimitiveCornerRadius.Medium
    val Large = SextouPrimitiveCornerRadius.Large
    val Full = SextouPrimitiveCornerRadius.Full

    val Control = Medium
    val Chip = Full
    val Surface = Large
    val AppShell = Large
    val Input = 14.dp
    val SearchBar = 16.dp
    val InputAction = 10.dp
    val ButtonLarge = 16.dp
}

val SextouShapes = Shapes(
    extraSmall = RoundedCornerShape(SextouCornerRadius.Control),
    small = RoundedCornerShape(SextouCornerRadius.Chip),
    medium = RoundedCornerShape(SextouCornerRadius.Surface),
    large = RoundedCornerShape(SextouCornerRadius.Surface),
    extraLarge = RoundedCornerShape(SextouCornerRadius.AppShell),
)

object SextouInputShapes {
    val TextInput = RoundedCornerShape(SextouCornerRadius.Input)
    val SearchBar = RoundedCornerShape(SextouCornerRadius.SearchBar)
    val Action = RoundedCornerShape(SextouCornerRadius.InputAction)
}
