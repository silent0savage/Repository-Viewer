package com.example.repoviewer.data.repository

import com.example.repoviewer.data.model.ReadmeDto
import com.example.repoviewer.data.model.RepoDto
import kotlin.io.encoding.Base64

// модель для экрана списка репозитория
data class RepoListUI(
    val owner: String,
    val repo: String,
    val language: String?,
    val description: String?
)

// модель для экрана информации о репозитории
data class RepoInfoUI(
    val repo: String,
    val url: String,
    val license: String?,
    val stars: Int,
    val forks: Int,
    val watchers: Int
)

fun RepoDto.toListUI() = RepoListUI(
    owner = owner.login,
    repo = name,
    language = language,
    description = description
)

fun RepoDto.toInfoUI() = RepoInfoUI(
    repo = name,
    url = htmlUrl,
    license = license?.name,
    stars = stargazersCount,
    forks = forksCount,
    watchers = watchersCount
)

fun ReadmeDto.decodeContent() : String =
    // декодирует в ByteArray и преобразует в обычную UTF-8 строку
    Base64.Mime.decode(content).decodeToString()