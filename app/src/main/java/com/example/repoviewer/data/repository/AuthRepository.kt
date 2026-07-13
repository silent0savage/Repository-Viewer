package com.example.repoviewer.data.repository

import com.example.repoviewer.data.remote.GithubApi
import com.example.repoviewer.data.storage.KeyValueStorage

class AuthRepository (
    private val api: GithubApi,
    private val storage: KeyValueStorage
) {
    // проверяет правильность токена
    suspend fun signIn(token: String): Result<Unit> = runCatching {
        api.signIn(token)
        storage.saveToken(token)
    }

    fun getToken(): String? = storage.getToken()

    fun hasToken() : Boolean {
        return storage.getToken() != null
    }
    fun signOut() = storage.clearToken()
}