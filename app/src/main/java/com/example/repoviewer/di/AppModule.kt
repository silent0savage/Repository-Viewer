package com.example.repoviewer.di

import com.example.repoviewer.data.remote.GithubApi
import com.example.repoviewer.data.repository.AuthRepository
import com.example.repoviewer.data.repository.GithubRepository
import com.example.repoviewer.data.storage.KeyValueStorage
import com.example.repoviewer.ui.auth.AuthViewModel
import com.example.repoviewer.ui.info.InfoViewModel
import com.example.repoviewer.ui.list.ListViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
        HttpClient(OkHttp) {
            expectSuccess = true

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    }
                )
            }
        }
    }

    single {
        GithubApi(client = get())
    }

    single {
        KeyValueStorage(context = androidContext())
    }

    single {
        AuthRepository(api = get(), storage = get())
    }

    single {
        GithubRepository(api = get(), storage = get())
    }

    viewModel {
        AuthViewModel(authRepository = get())
    }

    viewModel {
        ListViewModel(authRepository = get(), githubRepository = get())
    }

    viewModel { parameters ->
        InfoViewModel(savedStateHandle = parameters.get(), githubRepository = get())
    }
}