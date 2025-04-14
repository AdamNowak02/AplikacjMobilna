package pl.wsei.pam.lab06

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pl.wsei.pam.lab06.model.TodoTask
import java.util.Calendar

class NotificationHandler(private val context: Context) {

    private val channelId = "simple_channel_id"
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmRequestCode = 999

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Simple Channel"
            val descriptionText = "Channel for simple notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // FUNKCJA do natychmiastowego testowego powiadomienia (opcjonalna)
    fun showSimpleNotification() {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Powiadomienie")
            .setContentText("To jest proste powiadomienie.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(1001, builder.build())
        }
    }

    // Zmodyfikowana funkcja do ustawienia alarmu
    fun scheduleAlarmForTask(task: TodoTask, hoursBefore: Int = 24, repeatCount: Int = 3) {
        cancelAlarm() // anuluj poprzedni alarm

        // Konwersja daty zadania do LocalDateTime z domyślną godziną 8:00
        val calendar = Calendar.getInstance().apply {
            timeInMillis = 0
            set(task.deadline.year, task.deadline.monthValue - 1, task.deadline.dayOfMonth, 8, 0, 0) // Domyślnie o 8:00 rano
        }

        // Ustawiamy alarm na określoną datę i godzinę (8:00 rano)
        val triggerTimeMillis = calendar.timeInMillis - hoursBefore * 60 * 60 * 1000L // Dodajemy parametr hoursBefore (np. 24 godziny wcześniej)

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", task.title)
            putExtra("repeatCount", repeatCount)
            putExtra("channelId", channelId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            4 * 60 * 60 * 1000L, // Powtarzaj alarm co 4 godziny
            pendingIntent
        )
    }

    // Funkcja do anulowania alarmu
    fun cancelAlarm() {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
