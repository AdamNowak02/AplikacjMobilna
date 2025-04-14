package pl.wsei.pam.lab06

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.wsei.pam.lab06.data.AppDatabase
import pl.wsei.pam.lab06.data.DatabaseTodoTaskRepository
import pl.wsei.pam.lab06.data.TodoTaskRepository
import pl.wsei.pam.lab06.model.TodoTask
import pl.wsei.pam.lab06.settings.NotificationPreferences // Poprawiony import
import pl.wsei.pam.lab06.settings.SettingsScreen
import pl.wsei.pam.lab06.ui.theme.Lab06Theme
import java.time.LocalDate
import java.time.ZoneId

class Lab06Activity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Uprawnienia do powiadomień (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // 🔔 Tworzenie kanału powiadomień
        createNotificationChannel()

        setContent {
            Lab06Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    // 🔔 Tworzenie kanału powiadomień
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Deadline Channel"
            val descriptionText = "Channel for deadline notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // ⏰ Ustawienie alarmu
    fun scheduleAlarm(timeInMillis: Long) {
        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
            putExtra(titleExtra, "Deadline")
            putExtra(messageExtra, "Zbliża się termin zakończenia zadania")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // 🔁 Alarm powtarzający się co 4h
        val intervalMillis = 4 * 60 * 60 * 1000L // 4 godziny w milisekundach

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            intervalMillis,
            pendingIntent
        )
    }

    // ❌ Anulowanie istniejącego alarmu
    fun cancelAlarm() {
        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    // 📅 Ustawianie alarmu dla najbliższego zadania z uwzględnieniem preferencji (np. 1 dzień przed, godz. 8)
    fun scheduleNearestTaskAlarm(tasks: List<TodoTask>, prefs: NotificationPreferences) {
        val now = LocalDate.now()

        val nearest = tasks
            .filter { !it.isDone && it.deadline.isAfter(now) }
            .minByOrNull { it.deadline }

        nearest?.let {
            // Wczytanie ustawień preferencji (np. dzień przed, godzina przed, itd.)
            val notificationLeadTime = prefs.load().first // Pobranie liczby dni przed powiadomieniem
            val notificationHour = prefs.load().second // Pobranie godziny powiadomienia

            // Ustalamy czas alarmu na podstawie preferencji
            val alarmTime = it.deadline
                .minusDays(notificationLeadTime.toLong()) // Przesuwamy termin powiadomienia (np. 1 dzień przed)
                .atStartOfDay()
                .plusHours(notificationHour.toLong()) // Godzina ustawiona przez użytkownika
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            cancelAlarm() // Anulowanie poprzedniego alarmu
            scheduleAlarm(alarmTime) // Ustawienie nowego alarmu
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val todoTaskRepository: TodoTaskRepository =
        DatabaseTodoTaskRepository(AppDatabase.getInstance(context).taskDao())

    val prefs = NotificationPreferences(context) // Preferencje powiadomień

    NavHost(navController = navController, startDestination = "list") {
        composable("list") { ListScreen(navController) }
        composable("form") { FormScreen(navController, todoTaskRepository) }
        composable("settings") { SettingsScreen(navController, prefs) }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Lab06Theme {
        MainScreen()
    }
}
