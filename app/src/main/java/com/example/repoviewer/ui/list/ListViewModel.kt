package com.example.repoviewer.ui.list

import androidx.annotation.StringRes
import com.example.repoviewer.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repoviewer.data.repository.AuthRepository
import com.example.repoviewer.data.repository.GithubRepository
import com.example.repoviewer.data.repository.RepoListUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListViewModel(
    private val githubRepository: GithubRepository,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _state: MutableStateFlow<State> = MutableStateFlow(State.Loading)
    val state: StateFlow<State> = _state

    sealed interface State {
        object Empty : State
        object Loading : State
        data class Loaded(val repos: List<RepoListUI>) : State
        data class Error(
            @StringRes
            val error: Int
        ) : State
    }


    fun loadRepository() {
        viewModelScope.launch {
            _state.value = State.Loading
            val token = authRepository.getToken()

            if (token == null) {
                _state.value = State.Error(R.string.token_not_found)
                return@launch
            }

            githubRepository.getRepositories(token)
                .onSuccess { repos ->
                    _state.value = if (repos.isEmpty())
                        State.Empty
                    else
                        State.Loaded(repos)
                }

                .onFailure {
                    _state.value = State.Error(R.string.unknown)
                }

        }
    }
}
