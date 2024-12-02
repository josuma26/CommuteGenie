package org.example.mbta.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Route(
    @JsonProperty("short_name")
    val shortName: String,

    @JsonProperty("long_name")
    val longName: String,

    @JsonProperty("direction_names")
    val directionNames: List<String>,

    @JsonProperty("direction_destinations")
    val directionDestinations: List<String>
)

enum class RouteType {
    RED, GREEN, ORANGE, BLUE;

    companion object {
        fun fromId(id: String): RouteType = RouteType.valueOf(id.uppercase())
    }
}
