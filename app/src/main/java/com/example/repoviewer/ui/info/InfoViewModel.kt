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
    private val savedStateHandle: SavedStateHandle,
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

            githubRepository.getRepositoryDetails(owner, repo)
                .onSuccess { repoInfo ->

                    val loadedState = State.Loaded(
                        githubRepo = repoInfo,
                        readmeState = ReadmeState.Loading
                    )

                    _state.value = loadedState

                    githubRepository.getRepositoryReadme(owner, repo)
                        .onSuccess { markdown ->
                            _state.value = loadedState.copy(
                                readmeState =
                                    if (markdown.isBlank())
                                        ReadmeState.Empty
                                    else
                                        ReadmeState.Loaded(markdown)
                            )
                        }
                        .onFailure { error ->
                            _state.value =
                                if (error is ClientRequestException &&
                                    error.response.status == HttpStatusCode.NotFound
                                ) {
                                    loadedState.copy(
                                        readmeState = ReadmeState.Empty
                                    )
                                } else {
                                    loadedState.copy(
                                        readmeState = ReadmeState.Error(R.string.missing_readme)
                                    )
                                }
                        }
                }
                .onFailure {
                    _state.value = State.Error(R.string.unknown)
                }
        }
    }
}