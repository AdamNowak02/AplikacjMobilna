package pl.wsei.pam.lab06.model

import java.time.LocalDate

interface CurrentDateProvider {
    val currentDate: LocalDate
}
