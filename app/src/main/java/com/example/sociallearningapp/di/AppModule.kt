package com.example.sociallearningapp.di

import com.example.sociallearningapp.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    @Singleton
    fun provideQuizRepository(): QuizRepository {
        return QuizRepository()
    }

    @Provides
    @Singleton
    fun provideTaskRepository(): TaskRepository {
        return TaskRepository()
    }

    @Provides
    @Singleton
    fun provideChatRepository(): ChatRepository {
        return ChatRepository()
    }
}

