package com.klitsie.dataloading.example

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klitsie.dataloading.LoadingResultScreen

@Composable
fun ExampleScreen(
	hideMessage: () -> Unit,
	showMessage: (String) -> Unit,
	modifier: Modifier = Modifier,
	viewModel: ExampleViewModel = viewModel(),
) {
	val screenState by viewModel.screenState.collectAsState()
	val event by viewModel.event.collectAsState()
	LaunchedEffect(event) {
		when (event) {
			null -> return@LaunchedEffect
			ExampleEvent.ShowRefreshFailure -> showMessage("Refresh went wrong!")
		}
		viewModel.consumeEvent()
	}
	LoadingResultScreen(
		modifier = modifier,
		onRefresh = viewModel::refresh,
		loadingResult = screenState,
		content = { data, isLoading ->
			Column(
				modifier = Modifier
					.fillMaxSize(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = spacedBy(8.dp, Alignment.CenterVertically),
			) {
				Text(text = data)
				Button(onClick = {
					hideMessage()
					viewModel.refresh()
				}) {
					AnimatedVisibility(visible = isLoading) {
						CircularProgressIndicator(
							color = LocalContentColor.current,
							modifier = Modifier
								.padding(end = 8.dp)
								.size(24.dp)
						)
					}
					Text("Refresh")
				}
			}
		},
	)
}

