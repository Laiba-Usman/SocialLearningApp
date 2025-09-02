package com.example.sociallearningapp.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sociallearningapp.data.repository.TaskRepository
import com.example.sociallearningapp.domain.model.Task
import com.example.sociallearningapp.domain.model.TaskPriority
import com.example.sociallearningapp.domain.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _taskState = MutableStateFlow(TaskState())
    val taskState: StateFlow<TaskState> = _taskState.asStateFlow()

    private val _selectedFilter = MutableStateFlow(TaskStatus.ALL)

    private val allTasks = taskRepository.getTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tasks = combine(allTasks, _selectedFilter) { tasks, filter ->
        when (filter) {
            TaskStatus.ALL -> tasks
            TaskStatus.PENDING -> tasks.filter { it.status == TaskStatus.PENDING }
            TaskStatus.COMPLETED -> tasks.filter { it.status == TaskStatus.COMPLETED }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addTask(title: String, description: String, priority: TaskPriority) {
        viewModelScope.launch {
            _taskState.value = _taskState.value.copy(isLoading = true, error = null)
            try {
                val task = Task(
                    id = generateTaskId(),
                    title = title,
                    description = description,
                    priority = priority,
                    status = TaskStatus.PENDING,
                    createdAt = System.currentTimeMillis()
                )
                taskRepository.addTask(task)
                _taskState.value = _taskState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _taskState.value = _taskState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun toggleTaskComplete(taskId: String) {
        viewModelScope.launch {
            try {
                taskRepository.toggleTaskComplete(taskId)
            } catch (e: Exception) {
                _taskState.value = _taskState.value.copy(error = e.message)
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(taskId)
            } catch (e: Exception) {
                _taskState.value = _taskState.value.copy(error = e.message)
            }
        }
    }

    fun filterTasks(status: TaskStatus) {
        _selectedFilter.value = status
    }

    private fun generateTaskId(): String {
        return "task_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

