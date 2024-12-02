package org.example.model

import java.time.Instant

data class Trip(
    val option: Option,
    val departureTime: Instant,
    val arrivalTime: Instant
)
