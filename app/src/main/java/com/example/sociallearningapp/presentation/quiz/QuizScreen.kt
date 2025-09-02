package com.example.sociallearningapp.presentation.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sociallearningapp.domain.model.QuizResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel = hiltViewModel()
) {
    val quizState by viewModel.quizState.collectAsState()
    val quizHistory by viewModel.quizHistory.collectAsState()

    when {
        quizState.isQuizActive -> {
            QuizActiveScreen(
                quizState = quizState,
                onAnswerSelected = viewModel::selectAnswer,
                onNextQuestion = viewModel::nextQuestion,
                onFinishQuiz = viewModel::finishQuiz
            )
        }
        quizState.showResults -> {
            QuizResultScreen(
                score = quizState.score,
                totalQuestions = quizState.questions.size,
                onStartNewQuiz = viewModel::startQuiz,
                onBackToHome = viewModel::resetQuiz
            )
        }
        else -> {
            QuizHomeScreen(
                quizHistory = quizHistory,
                onStartQuiz = viewModel::startQuiz
            )
        }
    }
}

@Composable
fun QuizActiveScreen(
    quizState: QuizState,
    onAnswerSelected: (String) -> Unit,
    onNextQuestion: () -> Unit,
    onFinishQuiz: () -> Unit
) {
    val currentQuestion = quizState.currentQuestion

    if (currentQuestion != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Timer and Progress
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Question ${quizState.currentQuestionIndex + 1}/${quizState.questions.size}")
                Text("Time: ${quizState.timeRemaining}s")
            }

            LinearProgressIndicator(
                progress = (quizState.currentQuestionIndex + 1) / quizState.questions.size.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Question
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = currentQuestion.question,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            // Options
            currentQuestion.options.forEach { option ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { onAnswerSelected(option) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (quizState.selectedAnswer == option)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (quizState.currentQuestionIndex == quizState.questions.size - 1) {
                        onFinishQuiz()
                    } else {
                        onNextQuestion()
                    }
                },
                enabled = quizState.selectedAnswer.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (quizState.currentQuestionIndex == quizState.questions.size - 1)
                        "Finish Quiz" else "Next Question"
                )
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    score: Int,
    totalQuestions: Int,
    onStartNewQuiz: () -> Unit,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quiz Complete!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Your Score: $score/$totalQuestions",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = onStartNewQuiz,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Start New Quiz")
        }

        OutlinedButton(
            onClick = onBackToHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
fun QuizHomeScreen(
    quizHistory: List<QuizResult>,
    onStartQuiz: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Quiz Center",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onStartQuiz,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text("Start New Quiz")
        }

        if (quizHistory.isNotEmpty()) {
            Text(
                text = "Quiz History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(quizHistory) { result ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Score: ${result.score}/${result.totalQuestions}",
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Date: ${result.timestamp}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

