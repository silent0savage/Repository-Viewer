package com.example.repoviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.repoviewer.data.repository.AuthRepository
import com.example.repoviewer.ui.components.AppTopBar
import com.example.repoviewer.ui.components.TopBarState
import com.example.repoviewer.ui.navigation.AppNavHost
import com.example.repoviewer.ui.navigation.Screen
import com.example.repoviewer.ui.theme.RepoViewerTheme
import org.koin.compose.koinInject


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepoViewerTheme {
                StartApp()
            }
        }
    }
    @Composable
    fun StartApp() {
        val authRepository: AuthRepository = koinInject()
        val navController = rememberNavController()
        var topBarState by remember { mutableStateOf(TopBarState()) }
        Scaffold(
            topBar = {
                AppTopBar(
                    state = topBarState,
                    onBack = { navController.popBackStack() },
                    onLogOut = {
                        authRepository.signOut()

                        navController.navigate(Screen.Auth.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        ) { padding ->
            AppNavHost(
                navController,
                modifier = Modifier.padding(paddingValues = padding),
                onTopBarStateChanged = {
                    topBarState = it
                },
                // проверка залогинен ли пользователь
                startDestination =  if (authRepository.hasToken())
                    Screen.RepoList.route
                else
                    Screen.Auth.route
            )
        }
    }
}