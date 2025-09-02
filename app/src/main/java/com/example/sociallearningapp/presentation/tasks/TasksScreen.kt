package com.example.sociallearningapp.presentation.tasks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sociallearningapp.domain.model.Task
import com.example.sociallearningapp.domain.model.TaskPriority
import com.example.sociallearningapp.domain.model.TaskStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TasksViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val taskState by viewModel.taskState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(TaskStatus.ALL) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tasks",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }

        // Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskStatus.values().forEach { status ->
                FilterChip(
                    selected = selectedFilter == status,
                    onClick = {
                        selectedFilter = status
                        viewModel.filterTasks(status)
                    },
                    label = {
                        Text(
                            when (status) {
                                TaskStatus.ALL -> "All"
                                TaskStatus.PENDING -> "Pending"
                                TaskStatus.COMPLETED -> "Done"
                            }
                        )
                    }
                )
            }
        }

        // Tasks List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onToggleComplete = { viewModel.toggleTaskComplete(task.id) },
                    onDelete = { viewModel.deleteTask(task.id) },
                    onEdit = { /* TODO: Implement edit */ }
                )
            }
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onAddTask = { title, description, priority ->
                viewModel.addTask(title, description, priority)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = task.status == TaskStatus.COMPLETED,
                    onCheckedChange = { onToggleComplete() }
                )

                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (task.status == TaskStatus.COMPLETED)
                            TextDecoration.LineThrough else TextDecoration.None
                    )

                    if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    // Priority Badge
                    if (task.priority != TaskPriority.LOW) {
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = task.priority.name,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when (task.priority) {
                                    TaskPriority.HIGH -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                    TaskPriority.MEDIUM -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    TaskPriority.LOW -> MaterialTheme.colorScheme.surface
                                }
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (String, String, TaskPriority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Text("Priority:", fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.values().forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddTask(title, description, priority) },
                enabled = title.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
