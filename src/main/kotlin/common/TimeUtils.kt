package org.example.common

import java.time.*
import java.time.format.DateTimeFormatter

object TimeUtils {

    val ZONE_ID = ZoneId.of("America/New_York")
    val ZONE_OFFSET = ZoneOffset.ofHours(-5)

    fun toLocalDateTime(instant: Instant): LocalDateTime {
        return instant.atZone(ZONE_ID).toLocalDateTime()
    }

    fun timeString(instant: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        return formatter.format(
            toLocalDateTime(instant)
        )
    }

    fun localDateWithTime(time: LocalTime): Instant {
        return ZonedDateTime.now(ZONE_ID).toLocalDate().atTime(time).toInstant(ZONE_OFFSET)
    }
}