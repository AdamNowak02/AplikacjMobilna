// NotificationPreferences.kt

package pl.wsei.pam.lab06.settings

import android.content.Context
import android.content.SharedPreferences

class NotificationPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun save(hoursBefore: Int, repeatCount: Int) {
        prefs.edit()
            .putInt("hoursBefore", hoursBefore)
            .putInt("repeatCount", repeatCount)
            .apply()
    }

    fun load(): Pair<Int, Int> {
        val hoursBefore = prefs.getInt("hoursBefore", 24)
        val repeatCount = prefs.getInt("repeatCount", 3)
        return Pair(hoursBefore, repeatCount)
    }

    // Metoda do uzyskania liczby dni przed powiadomieniem
    fun getNotificationLeadTime(): Int {
        return prefs.getInt("notificationLeadTime", 1) // Wartość domyślna 1 dzień przed
    }

    // Metoda do uzyskania godziny powiadomienia
    fun getNotificationHour(): Int {
        return prefs.getInt("notificationHour", 8) // Wartość domyślna godzina 8 rano
    }
}
