package pl.wsei.pam.lab06.model

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object LocalDateConverter {

    fun toMillis(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun fromMillis(millis: Long): LocalDate {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}
