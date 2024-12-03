package org.example.model

import org.example.common.TimeUtils
import java.time.Duration
import java.time.Instant
import java.time.LocalTime

fun interface TimeConstraint {
    fun get(): Instant

    companion object {
        fun fromNow(offset: Duration) = TimeConstraint { Instant.now() + offset }

        fun dailyRun(time: LocalTime) = TimeConstraint {
            TimeUtils.localDateWithTime(time)
        }
    }
}

data class Constraint(
    val option: Option,

    val minDepartureTime: TimeConstraint,

    val maxDepartureTime: TimeConstraint
)

