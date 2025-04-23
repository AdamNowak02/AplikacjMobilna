package pl.wsei.pam.lab06.settings

import android.content.Context
import android.content.SharedPreferences

class NotificationPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun save(hoursBefore: Int, repeatCount: Int, daysBefore: Int) {
        prefs.edit()
            .putInt("hoursBefore", hoursBefore)
            .putInt("repeatCount", repeatCount)
            .putInt("notificationLeadTime", daysBefore)
            .apply()
    }

    fun load(): Triple<Int, Int, Int> {
        val hoursBefore = prefs.getInt("hoursBefore", 24)
        val repeatCount = prefs.getInt("repeatCount", 3)
        val daysBefore = prefs.getInt("notificationLeadTime", 1)
        return Triple(hoursBefore, repeatCount, daysBefore)
    }

    fun getNotificationLeadTime(): Int {
        return prefs.getInt("notificationLeadTime", 1)
    }

    fun getNotificationHour(): Int {
        return prefs.getInt("notificationHour", 8)
    }
}
