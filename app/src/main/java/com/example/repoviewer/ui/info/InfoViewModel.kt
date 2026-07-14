package com.example.repoviewer.ui.info

import androidx.annotation.StringRes
import com.example.repoviewer.R
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repoviewer.data.repository.GithubRepository
import com.example.repoviewer.data.repository.RepoInfoUI
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InfoViewModel(
    savedStateHandle: SavedStateHandle,
    private val githubRepository: GithubRepository,
): ViewModel() {
    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state
    val owner: String = checkNotNull(savedStateHandle["owner"])
    val repo: String = checkNotNull(savedStateHandle["repo"])

    sealed interface State {
        object Loading : State
        data class Error(val error: Int) : State
        data class Loaded(
            val githubRepo: RepoInfoUI,
            val readmeState: ReadmeState
        ) : State
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState
        data class Error(
            @StringRes
            val error: Int
        ) : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }

    fun loadRepoInfo() {
        viewModelScope.launch {

            _state.value = State.Loading

            val repoInfo = githubRepository
                .getRepositoryDetails(owner, repo)
                .getOrElse {
                    _state.value = State.Error(R.string.unknown)
                    return@launch
                }

            val loadedState = State.Loaded(
                githubRepo = repoInfo,
                readmeState = ReadmeState.Loading
            )

            _state.value = loadedState

            val readmeResult = githubRepository.getRepositoryReadme(owner, repo)

            _state.value = loadedState.copy(
                readmeState = readmeResult.fold(
                    onSuccess = { markdown ->
                        if (markdown.isBlank()) {
                            ReadmeState.Empty
                        } else {
                            ReadmeState.Loaded(markdown)
                        }
                    },
                    onFailure = { error ->
                        if (error is ClientRequestException &&
                            error.response.status == HttpStatusCode.NotFound
                        ) {
                            ReadmeState.Empty
                        } else {
                            ReadmeState.Error(R.string.missing_readme)
                        }
                    }
                )
            )
        }
    }
}