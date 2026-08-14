package com.sextou.domain.places.model

enum class BusinessStatus {
    OPERATIONAL,
    CLOSED_TEMPORARILY,
    CLOSED_PERMANENTLY,
    UNKNOWN,
}

enum class PlaceAttribute {
    YES,
    NO,
    UNKNOWN,
    NOT_AVAILABLE,
}

data class Money(
    val currencyCode: String,
    val units: Long,
    val nanos: Int,
)

data class PriceRange(
    val start: Money?,
    val end: Money?,
)

data class AddressComponent(
    val longName: String,
    val shortName: String?,
    val types: List<String>,
)

data class PostalAddress(
    val regionCode: String?,
    val languageCode: String?,
    val postalCode: String?,
    val sortingCode: String?,
    val administrativeArea: String?,
    val locality: String?,
    val sublocality: String?,
    val addressLines: List<String>,
    val recipients: List<String>,
    val organization: String?,
)

data class PlaceAuthor(
    val name: String,
    val profileUri: String?,
    val photoUri: String?,
)
