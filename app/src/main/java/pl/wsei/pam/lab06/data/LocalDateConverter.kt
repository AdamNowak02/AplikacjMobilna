package pl.wsei.pam.lab06.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateConverter {

    companion object {
        const val pattern = "yyyy-MM-dd"
    }

    @TypeConverter
    fun fromDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern(pattern))
    }

    @TypeConverter
    fun toDate(str: String): LocalDate {
        return LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern))
    }
}
