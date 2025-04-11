package pl.wsei.pam.lab06

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.wsei.pam.lab06.data.AppDatabase
import pl.wsei.pam.lab06.data.DatabaseTodoTaskRepository
import pl.wsei.pam.lab06.data.TodoTaskRepository
import pl.wsei.pam.lab06.ui.theme.Lab06Theme

class Lab06Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Tworzymy instancję repozytorium
    val context = LocalContext.current // Pobranie kontekstu
    val todoTaskRepository: TodoTaskRepository = DatabaseTodoTaskRepository(AppDatabase.getInstance(context).taskDao())

    NavHost(navController = navController, startDestination = "list") {
        composable("list") { ListScreen(navController, todoTaskRepository) }
        composable("form") { FormScreen(navController, todoTaskRepository) }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Lab06Theme {
        MainScreen()
    }
}
