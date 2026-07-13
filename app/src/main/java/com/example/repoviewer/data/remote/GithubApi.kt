package com.example.repoviewer.data.remote

import com.example.repoviewer.data.model.ReadmeDto
import com.example.repoviewer.data.model.RepoDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

const val BASE_URL = "https://api.github.com"

class GithubApi(
    private val client: HttpClient
) {
    suspend fun fetchRepositories(token: String): List<RepoDto> {
        return client
            .get("$BASE_URL/user/repos") {
                header("Authorization", "Bearer $token")
                parameter("per_page", 10)
                parameter("sort", "updated")
            }
            .body()
    }

    suspend fun fetchRepository(
        owner: String,
        repo: String,
        token: String
    ): RepoDto = client.get("$BASE_URL/repos/$owner/$repo") {
        header("Authorization", "Bearer $token")
    }.body()

    suspend fun fetchReadMe(
        owner: String,
        repo: String,
        token: String
    ): ReadmeDto = client.get("$BASE_URL/repos/$owner/$repo/readme") {
        header("Authorization", "Bearer $token")
    }.body()

    suspend fun signIn(token: String) {
        try {
            client.get("$BASE_URL/user") {
                header("Authorization", "Bearer $token")
            }.also { response ->
                if (!response.status.isSuccess()) {
                    throw UnauthorizedException()
                }
            }
        } catch (error: ClientRequestException) {
            if (error.response.status == HttpStatusCode.Unauthorized) {
                throw UnauthorizedException()
            }
            throw error
        }
    }
}

class UnauthorizedException : Exception()