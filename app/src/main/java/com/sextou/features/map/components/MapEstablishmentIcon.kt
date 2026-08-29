package com.sextou.features.map.components

import androidx.annotation.DrawableRes
import com.sextou.designsystem.R as DesignSystemR
import java.util.Locale

private val establishmentIconByPlaceType = mapOf(
    "bar" to DesignSystemR.drawable.ic_sextou_establishment_bar,
    "bar_and_grill" to DesignSystemR.drawable.ic_sextou_establishment_bar_e_restaurante,
    "karaoke" to DesignSystemR.drawable.ic_sextou_establishment_karaoke,
    "wine_bar" to DesignSystemR.drawable.ic_sextou_establishment_adega,
    "winery" to DesignSystemR.drawable.ic_sextou_establishment_adega,
    "liquor_store" to DesignSystemR.drawable.ic_sextou_establishment_deposito_de_bebidas,
    "warehouse_store" to DesignSystemR.drawable.ic_sextou_establishment_deposito_de_bebidas,
    "wholesaler" to DesignSystemR.drawable.ic_sextou_establishment_deposito_de_bebidas,
    "steak_house" to DesignSystemR.drawable.ic_sextou_establishment_churrascaria,
    "barbecue_restaurant" to DesignSystemR.drawable.ic_sextou_establishment_churrascaria,
    "restaurant" to DesignSystemR.drawable.ic_sextou_establishment_restaurante,
    "hamburger_restaurant" to DesignSystemR.drawable.ic_sextou_establishment_hamburgueria,
    "fast_food_restaurant" to DesignSystemR.drawable.ic_sextou_establishment_hamburgueria_fast_food,
    "pizza_restaurant" to DesignSystemR.drawable.ic_sextou_establishment_pizzaria,
    "beer_garden" to DesignSystemR.drawable.ic_sextou_establishment_chopperia,
    "brewery" to DesignSystemR.drawable.ic_sextou_establishment_chopperia,
    "brewpub" to DesignSystemR.drawable.ic_sextou_establishment_chopperia,
    "cafe" to DesignSystemR.drawable.ic_sextou_establishment_cafe,
    "coffee_shop" to DesignSystemR.drawable.ic_sextou_establishment_cafe,
    "bakery" to DesignSystemR.drawable.ic_sextou_establishment_padaria,
    "pub" to DesignSystemR.drawable.ic_sextou_establishment_pub,
    "cocktail_bar" to DesignSystemR.drawable.ic_sextou_establishment_bar_de_coqueteis,
    "sports_bar" to DesignSystemR.drawable.ic_sextou_establishment_bar_esportivo,
    "night_club" to DesignSystemR.drawable.ic_sextou_establishment_balada,
    "live_music_venue" to DesignSystemR.drawable.ic_sextou_establishment_musica_ao_vivo,
    "breakfast_restaurant" to DesignSystemR.drawable.ic_sextou_establishment_cafe_da_manha,
    "brunch_restaurant" to DesignSystemR.drawable.ic_sextou_establishment_cafe_da_manha,
    "confectionery" to DesignSystemR.drawable.ic_sextou_establishment_doceria_confeitaria,
    "dessert_shop" to DesignSystemR.drawable.ic_sextou_establishment_doceria_confeitaria,
    "pastry_shop" to DesignSystemR.drawable.ic_sextou_establishment_doceria_confeitaria,
    "cake_shop" to DesignSystemR.drawable.ic_sextou_establishment_doceria_confeitaria,
    "ice_cream_shop" to DesignSystemR.drawable.ic_sextou_establishment_sorveteria,
)

@DrawableRes
internal fun mapEstablishmentIconResource(
    primaryType: String?,
    types: List<String>,
): Int {
    val candidates = buildList {
        primaryType?.let(::add)
        addAll(types)
    }

    return candidates
        .asSequence()
        .map { it.trim().lowercase(Locale.ROOT) }
        .mapNotNull { establishmentIconByPlaceType[it] }
        .firstOrNull()
        ?: DesignSystemR.drawable.ic_sextou_establishment_restaurante
}
