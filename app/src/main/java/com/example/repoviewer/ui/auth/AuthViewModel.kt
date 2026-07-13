package com.example.repoviewer.ui.auth

import androidx.annotation.StringRes
import com.example.repoviewer.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repoviewer.data.remote.UnauthorizedException
import com.example.repoviewer.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _state = MutableStateFlow<State>(State.Idle)
    val state: StateFlow<State> = _state

    private val _actions = MutableSharedFlow<Action>()
    val actions: Flow<Action> = _actions


    fun onSignButtonPressed(token: String) {
        if (token.isBlank()) {
            _state.value = State.InvalidInput(R.string.empty_value)
            return
        }
        viewModelScope.launch {
            _state.value = State.Loading
            authRepository.signIn(token)
                .onSuccess { _actions.emit(Action.RouteToMain) }
                .onFailure { error ->
                    when(error) {
                        is UnauthorizedException -> {
                            _state.value = State.InvalidInput(R.string.invalid_token)
                            _actions.emit(
                                Action.ShowError(R.string.invalid_token)
                            )
                        }

                        else -> {
                            _state.value = State.Idle
                            _actions.emit(
                                Action.ShowError(R.string.unknown)
                            )
                        }
                    }
                }
        }
    }

    sealed interface State {
        object Idle : State
        object Loading : State
        data class InvalidInput(
            @StringRes
            val reason: Int
        ) : State
    }

    sealed interface Action {
        data class ShowError(
            @StringRes val message: Int
        ) : Action
        object RouteToMain : Action
    }
}