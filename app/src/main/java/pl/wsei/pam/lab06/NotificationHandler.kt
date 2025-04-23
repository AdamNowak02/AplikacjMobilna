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
import pl.wsei.pam.lab06.settings.NotificationPreferences
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

    /** Pozwala wywołać alarm z pojedynczym parametrem — pobierze preferencje wewnętrznie */
    fun scheduleAlarmForTask(task: TodoTask) {
        val prefs = NotificationPreferences(context)
        scheduleAlarmForTask(task, prefs)
    }

    /**
     * Oryginalna metoda, która ustawia powtarzalny alarm
     * @param task zadanie
     * @param preferences ustawienia godzin/dni/repeatCount
     */
    fun scheduleAlarmForTask(
        task: TodoTask,
        preferences: NotificationPreferences
    ) {
        cancelAlarm()

        val daysBefore = preferences.getNotificationLeadTime()
        val hoursBefore = preferences.getNotificationHour()
        val repeatCount = preferences.load().second

        // ustawiamy datę o godzinie hoursBefore tego dnia
        val calendar = Calendar.getInstance().apply {
            set(
                task.deadline.year,
                task.deadline.monthValue - 1,
                task.deadline.dayOfMonth,
                hoursBefore,
                0,
                0
            )
        }

        // odejmujemy dniBefore dni
        val triggerTimeMillis = calendar.timeInMillis - (daysBefore * 24 * 60 * 60 * 1000L)

        // jeśli już minęło, pokazujemy od razu
        if (System.currentTimeMillis() >= triggerTimeMillis) {
            showNotificationNow(task.title)
            return
        }

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
            4 * 60 * 60 * 1000L, // co 4 godziny
            pendingIntent
        )
    }

    private fun showNotificationNow(title: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Zbliżający się termin!")
            .setContentText("Zadanie: $title ma termin wkrótce!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify((0..9999).random(), builder.build())
        }
    }

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

    /** Proste testowe powiadomienie */
    fun showSimpleNotification() {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Powiadomienie")
            .setContentText("To jest proste powiadomienie.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(1001, builder.build())
        }
    }
}
