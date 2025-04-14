package pl.wsei.pam.lab06

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHandler(private val context: Context) {

    private val channelId = "simple_channel_id"

    init {
        createNotificationChannel()
    }

    // Funkcja do tworzenia kanału powiadomień (tylko raz)
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

    // Funkcja do wyświetlania powiadomienia
    fun showSimpleNotification() {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Powiadomienie")
            .setContentText("To jest proste powiadomienie.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)

        // Sprawdzamy uprawnienia do powiadomień w Androidzie 13+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(1001, builder.build())
        } else {
            // Jeśli brak uprawnień, nic nie robimy (można dodać komunikat do użytkownika)
        }
    }
}
