package com.example.cleantodoapp

import android.app.Application
import com.example.cleantodoapp.di.AppContainer

class CleanTodoApplication : Application() {
    val appContainer: AppContainer by lazy { AppContainer(this) }
}
