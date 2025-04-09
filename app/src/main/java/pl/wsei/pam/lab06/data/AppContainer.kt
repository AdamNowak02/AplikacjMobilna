package pl.wsei.pam.lab06.data

import android.content.Context

// Interfejs kontenera zależności
interface AppContainer {
    // Tutaj będą właściwości np. repozytorium
}

// Implementacja kontenera
class AppDataContainer(private val context: Context) : AppContainer {
    // Tu będą implementacje zależności np. inicjalizacja bazy danych, repozytoriów itd.
}
