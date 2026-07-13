package com.example.repoviewer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.repoviewer.R
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.example.repoviewer.ui.auth.AuthScreen
import com.example.repoviewer.ui.components.TopBarState
import com.example.repoviewer.ui.info.InfoScreen
import com.example.repoviewer.ui.list.ListScreen
import com.example.repoviewer.ui.navigation.Screen.Auth
import com.example.repoviewer.ui.navigation.Screen.RepoInfo
import com.example.repoviewer.ui.navigation.Screen.RepoList

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier,
    onTopBarStateChanged: (TopBarState) -> Unit,
    startDestination: String
) {

    val repositoriesTitle = stringResource(R.string.repositories)
    val navGraph = remember (navController) {
        navController.createGraph(
            startDestination = startDestination
        ) {
            composable(Auth.route){
                LaunchedEffect(Unit) {
                    onTopBarStateChanged(
                        TopBarState(
                            isVisible = false
                        )
                    )
                }
                AuthScreen(
                    onNavigateToReposList = { navController.navigate(RepoList.route)},
                )
            }
            composable(RepoList.route) {
                LaunchedEffect(Unit) {
                    onTopBarStateChanged(
                        TopBarState(
                            isVisible = true,
                            title = repositoriesTitle,
                            showBackButton = false
                        )
                    )
                }
                ListScreen (
                    onNavigateToReposInfo = { owner, repo ->
                        navController.navigate(
                            RepoInfo.createRoute(
                                owner = owner, repo = repo
                            )
                        )
                    }
                )
            }
            composable(
                route = RepoInfo.route,
                arguments = listOf(
                    navArgument("owner") {
                        type = NavType.StringType
                    },
                    navArgument("repo") {
                        type = NavType.StringType
                    }
                )
            )  {
                InfoScreen(
                    onTitleChanged = { state ->
                        onTopBarStateChanged(
                            TopBarState(
                                isVisible = true,
                                title = state.title,
                                showBackButton = true
                            )
                        )
                    }
                )
            }
        }
    }

    NavHost (
        navController = navController,
        graph = navGraph,
        modifier = modifier
    )
}