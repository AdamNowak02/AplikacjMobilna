package pl.wsei.pam.lab06.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController, prefs: NotificationPreferences) {  // Zmieniona nazwa klasy
    // Załaduj zapisane dane z preferencji
    val (savedHoursBefore, savedRepeatCount) = prefs.load()

    // Przechowywanie wartości w stanie
    var hoursBefore by remember { mutableStateOf(savedHoursBefore.toString()) }
    var repeatCount by remember { mutableStateOf(savedRepeatCount.toString()) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ustawienia powiadomień", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = hoursBefore,
            onValueChange = { hoursBefore = it },
            label = { Text("Ile godzin przed deadlinem?") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = repeatCount,
            onValueChange = { repeatCount = it },
            label = { Text("Ile razy powtórzyć powiadomienie?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Zapisz dane do preferencji
            prefs.save(hoursBefore.toIntOrNull() ?: 24, repeatCount.toIntOrNull() ?: 3)
            // Przejdź z powrotem do poprzedniego ekranu
            navController.popBackStack()
        }) {
            Text("Zapisz")
        }
    }
}
