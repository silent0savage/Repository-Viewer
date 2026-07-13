package com.example.repoviewer.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    @Serializable
    object Auth : Screen("auth")
    @Serializable
    object RepoList : Screen("repositories")
    @Serializable
    object RepoInfo: Screen("repository/{owner}/{repo}") {
        fun createRoute(owner: String, repo: String) = "repository/${owner}/$repo"
    }
}