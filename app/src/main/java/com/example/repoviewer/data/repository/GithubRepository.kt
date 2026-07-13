package com.example.repoviewer.data.repository

import com.example.repoviewer.data.remote.GithubApi
import com.example.repoviewer.data.storage.KeyValueStorage

class GithubRepository(
    private val api: GithubApi,
    private val storage: KeyValueStorage
) {
    // работа с моделью репозитория для списка репозиториев
    suspend fun getRepositories(token: String): Result<List<RepoListUI>> = runCatching {
        val repos = api.fetchRepositories(token)
        repos.map { it.toListUI() }
    }

    // работа с моделью репозитория для деталей репозитория
    suspend fun getRepositoryDetails(
        owner: String,
        repo: String
    ): Result<RepoInfoUI> = runCatching {
        val token = requireNotNull(storage.getToken())
        api.fetchRepository(owner, repo, token).toInfoUI()
    }


    // получение репозитория
    suspend fun getRepositoryReadme(
        owner: String,
        repo: String
    ): Result<String> = runCatching {
        val token = requireNotNull(storage.getToken())
        api.fetchReadMe(owner, repo, token).decodeContent()
    }
}