package org.example.mbta.model

import com.fasterxml.jackson.databind.JsonNode

data class MbtaApiResponseItem(val id: String, val attributes: JsonNode, val relationships: JsonNode)

data class MbtaApiResponse(
    val data: List<MbtaApiResponseItem>
)
