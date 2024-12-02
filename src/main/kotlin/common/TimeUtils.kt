package org.example.common

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeUtils {

    val ZONE_ID = ZoneId.of("America/New_York")

    fun toLocalDateTime(instant: Instant): LocalDateTime {
        return instant.atZone(ZONE_ID).toLocalDateTime()
    }

    fun timeString(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        return formatter.format(
            toLocalDateTime(instant)
        )
    }
}