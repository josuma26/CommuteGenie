package org.example.model

import org.example.common.TimeUtils
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

fun interface TimeConstraint {
    fun get(): Instant

    companion object {
        fun fromNow(offset: Duration) = TimeConstraint { Instant.now() + offset }

        fun dailyRun(time: LocalTime) = TimeConstraint {
            LocalDate.now().atTime(time).atZone(TimeUtils.ZONE_ID).toInstant()
        }
    }
}

data class Constraint(
    val option: Option,

    val minDepartureTime: TimeConstraint,

    val maxDepartureTime: TimeConstraint
)

