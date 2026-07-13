package com.example.repoviewer.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repoviewer.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onNavigateToReposList: () -> Unit,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    //  запускаем коррутину внутри Compose для обработки action
    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
            when (action) {
                is AuthViewModel.Action.RouteToMain -> onNavigateToReposList()
                is AuthViewModel.Action.ShowError -> snackbarHostState.showSnackbar(context.getString(action.message))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 30.dp,
                        top = 100.dp,
                        bottom = 50.dp,
                        end = 30.dp
                    )
            ) {

                val invalidInput = state as? AuthViewModel.State.InvalidInput
                var token by remember { mutableStateOf("") }
                Row {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.github_invertocat_white),
                        contentDescription = null
                    )
                }

                Row(
                    Modifier.padding(top = 50.dp, bottom = 350.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Center
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = token,
                        onValueChange = { token = it },
                        singleLine = true,
                        label = { Text(text = stringResource(R.string.personal_access_token)) },
                        isError = invalidInput != null,
                        supportingText = {
                            invalidInput?.let {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(it.reason),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                }

                Row {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.auth_button)
                        ),
                        onClick = { viewModel.onSignButtonPressed(token) },
                        enabled = token.isNotBlank()
                    ) {
                        Text(
                            text = stringResource(R.string.sign_in),
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}