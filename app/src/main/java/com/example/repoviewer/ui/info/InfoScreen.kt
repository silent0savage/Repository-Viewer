package com.example.repoviewer.ui.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.repoviewer.R
import com.example.repoviewer.data.repository.RepoInfoUI
import com.example.repoviewer.ui.components.TopBarState
import com.example.repoviewer.ui.info.InfoViewModel.ReadmeState
import com.example.repoviewer.ui.info.InfoViewModel.State
import org.koin.androidx.compose.koinViewModel

@Composable
fun InfoScreen(
    onTitleChanged: (TopBarState) -> Unit,
    viewModel: InfoViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadRepoInfo()
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        when (val current = state) {

            State.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is State.Error -> {
                item {
                    Text(stringResource(current.error))
                }
            }

            is State.Loaded -> {
                onTitleChanged(TopBarState(title = current.githubRepo.repo))

                item {
                    RepoInfo(repoInfoUI = current.githubRepo)
                }
                item {
                    HorizontalDivider()
                }
                item {
                    ReadmeInfo(readmeState = current.readmeState)
                }
            }
        }
    }
}


@Composable
fun RepoInfo(repoInfoUI: RepoInfoUI) {
    val starsColor = colorResource(R.color.stars)
    val forksColor = colorResource(R.color.forks)
    val watchersColor = colorResource(R.color.watchers)

    val uriHandler = LocalUriHandler.current

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.link_svgrepo_com),
                contentDescription = "link"
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = repoInfoUI.url,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    uriHandler.openUri(repoInfoUI.url)
                })
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.scales_svgrepo_com),
                    contentDescription = "license"
                )
                Spacer(Modifier.width(4.dp))
                Text(repoInfoUI.license ?: stringResource(R.string.no_license))
            }

        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        {
            Column {
                Row {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.star_svgrepo_com),
                        tint = starsColor,
                        contentDescription = "stars"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = repoInfoUI.stars.toString(), color = starsColor)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.stars))
                }
            }

            Column {
                Row {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.git_fork_svgrepo_com),
                        tint = forksColor,
                        contentDescription = "forks"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = repoInfoUI.forks.toString(), color = forksColor)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.forks))
                }
            }

            Column {
                Row {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.eye_svgrepo_com),
                        tint = watchersColor,
                        contentDescription = "watchers"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = repoInfoUI.watchers.toString(), color = watchersColor)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.watchers))
                }
            }
        }
    }
}


@Composable
fun ReadmeInfo(readmeState: ReadmeState) {
    when (readmeState) {
        ReadmeState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ReadmeState.Error -> {
            Text(stringResource(readmeState.error))
        }

        is ReadmeState.Loaded -> {
            Text(modifier = Modifier.padding(16.dp),
                text = readmeState.markdown
            )
        }

        is ReadmeState.Empty -> {
            Box (
                Modifier.padding(15.dp)
            ) {
                Text(stringResource(R.string.missing_readme))
            }

        }
    }
}