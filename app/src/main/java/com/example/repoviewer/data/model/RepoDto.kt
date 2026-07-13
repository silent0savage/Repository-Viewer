package com.example.repoviewer.data.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDto (
    val name: String,
    val owner: OwnerDto,
    val license: LicenseDto? = null,
    val language: String? = null,
    val description: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("stargazers_count") val stargazersCount: Int,
    @SerialName("watchers_count") val watchersCount: Int,
    @SerialName("forks_count") val forksCount: Int
)