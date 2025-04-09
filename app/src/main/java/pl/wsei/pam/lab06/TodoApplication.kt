package pl.wsei.pam.lab06

import android.app.Application
import pl.wsei.pam.lab06.data.AppContainer
import pl.wsei.pam.lab06.data.AppDataContainer

class TodoApplication : Application() {

    // Tutaj będzie kontener DI
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this.applicationContext)
    }
}
