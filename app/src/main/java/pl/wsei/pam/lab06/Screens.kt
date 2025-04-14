package pl.wsei.pam.lab06

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import pl.wsei.pam.lab06.model.Priority
import pl.wsei.pam.lab06.model.TodoTask
import pl.wsei.pam.lab06.ui.viewmodel.AppViewModelProvider
import pl.wsei.pam.lab06.ui.viewmodel.ListViewModel
import java.time.LocalDate

// LISTA ZADAŃ
@Composable
fun ListScreen(
    navController: NavController,
    viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val listUiState by viewModel.listUiState.collectAsState()

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
                    contentDescription = "Dodaj zadanie",
                    modifier = Modifier.scale(1.5f)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(items = listUiState.items) { item ->
                ListItem(item = item)
            }
        }
    }
}

// ELEMENT LISTY
@Composable
fun ListItem(item: TodoTask, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(120.dp)
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Tytuł: ${item.title}")
            Text(text = "Data: ${item.deadline}")
            Text(text = "Priorytet: ${item.priority.name}")
            Text(text = "Wykonane: ${if (item.isDone) "Tak" else "Nie"}")
        }
    }
}

// FORMULARZ ZADANIA
@Composable
fun FormScreen(navController: NavController, todoTaskRepository: pl.wsei.pam.lab06.data.TodoTaskRepository) {
    var title by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(LocalDate.now()) }
    var priority by remember { mutableStateOf(Priority.Medium) }
    var isDone by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isDateValid = remember(deadline) {
        !deadline.isBefore(LocalDate.now())
    }

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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tytuł zadania") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Wybierz datę deadline:")
            Button(onClick = { showDatePicker = true }) {
                Text(text = deadline.toString())
            }

            if (showDatePicker) {
                val datePickerDialog = remember {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            deadline = LocalDate.of(year, month + 1, dayOfMonth)
                            showDatePicker = false
                        },
                        deadline.year,
                        deadline.monthValue - 1,
                        deadline.dayOfMonth
                    )
                }
                LaunchedEffect(Unit) {
                    datePickerDialog.show()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Wybierz priorytet:")
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = priority.name)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Priority.values().forEach { priorityItem ->
                    DropdownMenuItem(
                        onClick = {
                            priority = priorityItem
                            expanded = false
                        },
                        text = { Text(text = priorityItem.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = isDone,
                    onCheckedChange = { isDone = it }
                )
                Text(text = "Zadanie wykonane", modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val newTask = TodoTask(
                        title = title,
                        deadline = deadline,
                        isDone = isDone,
                        priority = priority
                    )
                    scope.launch {
                        todoTaskRepository.insertItem(newTask)
                    }
                    navController.navigate("list")
                },
                enabled = isDateValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zapisz zadanie")
            }
        }
    }
}
