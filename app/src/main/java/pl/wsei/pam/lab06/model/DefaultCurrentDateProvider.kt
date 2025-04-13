package pl.wsei.pam.lab06.model

import java.time.LocalDate

class DefaultCurrentDateProvider : CurrentDateProvider {
    override val currentDate: LocalDate
        get() = LocalDate.now()
}
