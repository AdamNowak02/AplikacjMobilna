package pl.wsei.pam.lab06

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ListScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Lista",
                showBackIcon = false,
                route = "form"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = { navController.navigate("form") }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add task",
                    modifier = Modifier.scale(1.5f)
                )
            }
        }
    ) { innerPadding ->
        Text(
            text = "Lista",
            modifier = Modifier.padding(innerPadding).padding(16.dp)
        )
    }
}

@Composable
fun FormScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Formularz",
                showBackIcon = true,
                route = "list"
            )
        }
    ) { innerPadding ->
        Text(
            text = "Formularz",
            modifier = Modifier.padding(innerPadding).padding(16.dp)
        )
    }
}
