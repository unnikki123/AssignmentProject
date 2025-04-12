package com.ukv.assignmentproject

import android.app.Application
import com.ukv.assignmentproject.di.AppContainer

class AssignmentProject : Application() {

    // Manual DI container
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
