package com.example.repoviewer

import android.app.Application
import com.example.repoviewer.di.appModule
import org.koin.core.context.startKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class RepoViewerApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // логирование зависимостей
            androidLogger()

            androidContext(this@RepoViewerApp)

            modules(appModule)
        }
    }
}