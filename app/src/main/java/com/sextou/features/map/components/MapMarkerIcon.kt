package com.sextou.features.map.components

import androidx.annotation.DrawableRes
import com.sextou.designsystem.R as DesignSystemR

@DrawableRes
internal fun mapMarkerIconResource(
    placeId: String,
    favoritePlaceIds: Set<String>,
    ignoredPlaceIds: Set<String>,
): Int = when {
    placeId in ignoredPlaceIds -> DesignSystemR.drawable.ic_sextou_map_marker_ignorar
    placeId in favoritePlaceIds -> DesignSystemR.drawable.ic_sextou_map_marker_bombando
    else -> DesignSystemR.drawable.ic_sextou_map_marker_listados
}
