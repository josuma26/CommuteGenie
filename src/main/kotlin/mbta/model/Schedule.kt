package org.example.mbta.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.databind.JsonNode
import java.time.Instant

data class Schedule(
    val id: String,
    val attributes: ScheduleNested,
    val relationships: JsonNode
) {
    data class ScheduleNested(
        @JsonProperty("arrival_time")
        val arrivalTime: Instant?,

        @JsonProperty("departure_time")
        val departureTime: Instant?,
    )

    val arrivalTime get() = attributes.arrivalTime
    val departureTime get() = attributes.departureTime
    val tripId get() = relationships.get("trip").get("data").get("id").textValue()
}
