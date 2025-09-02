package com.example.sociallearningapp.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sociallearningapp.data.repository.QuizRepository
import com.example.sociallearningapp.domain.model.Question
import com.example.sociallearningapp.domain.model.QuizResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizState(
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String = "",
    val score: Int = 0,
    val timeRemaining: Int = 10,
    val isQuizActive: Boolean = false,
    val showResults: Boolean = false
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val _quizHistory = MutableStateFlow<List<QuizResult>>(emptyList())
    val quizHistory: StateFlow<List<QuizResult>> = _quizHistory.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadQuizHistory()
    }

    private fun loadQuizHistory() {
        viewModelScope.launch {
            quizRepository.getQuizHistory().collect { history ->
                _quizHistory.value = history
            }
        }
    }

    fun startQuiz() {
        val questions = generateQuestions()
        _quizState.value = QuizState(
            questions = questions,
            isQuizActive = true,
            timeRemaining = 10
        )
        startTimer()
    }

    private fun generateQuestions(): List<Question> {
        return listOf(
            Question(
                id = "1",
                question = "What is the capital of France?",
                options = listOf("Paris", "London", "Berlin", "Madrid"),
                correctAnswer = "Paris"
            ),
            Question(
                id = "2",
                question = "Which planet is known as the Red Planet?",
                options = listOf("Venus", "Mars", "Jupiter", "Saturn"),
                correctAnswer = "Mars"
            ),
            Question(
                id = "3",
                question = "What is 2 + 2?",
                options = listOf("3", "4", "5", "6"),
                correctAnswer = "4"
            ),
            Question(
                id = "4",
                question = "Who wrote Romeo and Juliet?",
                options = listOf("Charles Dickens", "William Shakespeare", "Jane Austen", "Mark Twain"),
                correctAnswer = "William Shakespeare"
            ),
            Question(
                id = "5",
                question = "What is the largest ocean on Earth?",
                options = listOf("Atlantic", "Indian", "Arctic", "Pacific"),
                correctAnswer = "Pacific"
            )
        )
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_quizState.value.timeRemaining > 0 && _quizState.value.isQuizActive) {
                delay(1000)
                _quizState.value = _quizState.value.copy(
                    timeRemaining = _quizState.value.timeRemaining - 1
                )

                if (_quizState.value.timeRemaining <= 0) {
                    nextQuestion()
                }
            }
        }
    }

    fun selectAnswer(answer: String) {
        _quizState.value = _quizState.value.copy(selectedAnswer = answer)
    }

    fun nextQuestion() {
        val currentState = _quizState.value
        val currentQuestion = currentState.currentQuestion
        val isCorrect = currentQuestion?.correctAnswer == currentState.selectedAnswer

        val newScore = if (isCorrect) currentState.score + 1 else currentState.score
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex >= currentState.questions.size) {
            finishQuiz()
        } else {
            _quizState.value = currentState.copy(
                currentQuestionIndex = nextIndex,
                selectedAnswer = "",
                score = newScore,
                timeRemaining = 10
            )
            startTimer()
        }
    }

    fun finishQuiz() {
        timerJob?.cancel()
        val currentState = _quizState.value
        val currentQuestion = currentState.currentQuestion
        val isCorrect = currentQuestion?.correctAnswer == currentState.selectedAnswer
        val finalScore = if (isCorrect) currentState.score + 1 else currentState.score

        _quizState.value = currentState.copy(
            score = finalScore,
            isQuizActive = false,
            showResults = true
        )

        // Save quiz result
        saveQuizResult(finalScore, currentState.questions.size)
    }

    private fun saveQuizResult(score: Int, totalQuestions: Int) {
        viewModelScope.launch {
            val result = QuizResult(
                score = score,
                totalQuestions = totalQuestions,
                timestamp = System.currentTimeMillis()
            )
            quizRepository.saveQuizResult(result)
        }
    }

    fun resetQuiz() {
        timerJob?.cancel()
        _quizState.value = QuizState()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

