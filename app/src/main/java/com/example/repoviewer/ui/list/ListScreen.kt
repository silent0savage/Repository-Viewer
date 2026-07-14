package com.example.repoviewer.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.repoviewer.R
import com.example.repoviewer.data.repository.RepoListUI
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onNavigateToReposInfo : (String, String) -> Unit,
    viewModel: ListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRepository()
    }

    when (val currentState = state) {

        ListViewModel.State.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ListViewModel.State.Loaded -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = currentState.repos,
                    // эффективное обновление списка
                    key = { repo ->
                        "${repo.owner}/${repo.repo}"
                    }
                ) { repo ->
                    ListItem(repo, onClick = onNavigateToReposInfo)
                    HorizontalDivider()
                }
            }
        }

        is ListViewModel.State.Error -> {
            Text(stringResource(id = currentState.error))
        }

        is ListViewModel.State.Empty -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.missing_repos))
            }

        }
    }
}



@Composable
fun ListItem(
    repo: RepoListUI,
    onClick: (String, String) -> Unit
) {
    Card (
        onClick = {onClick(repo.owner, repo.repo)},
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        )  {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = repo.repo,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = repo.language.orEmpty(),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Row {
                Text(
                    text = repo.description.orEmpty(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }
    }
}