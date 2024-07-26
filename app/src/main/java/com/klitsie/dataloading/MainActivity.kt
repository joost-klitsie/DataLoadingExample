package com.klitsie.dataloading

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.klitsie.dataloading.example.ExampleScreen
import com.klitsie.dataloading.ui.theme.DataLoadingTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()
		setContent {
			DataLoadingTheme {
				val snackbarHostState = remember { SnackbarHostState() }
				val coroutineScope = rememberCoroutineScope()
				Scaffold(
					modifier = Modifier.fillMaxSize(),
					snackbarHost = { SnackbarHost(snackbarHostState) },
				) { innerPadding ->
					ExampleScreen(
						hideMessage = { snackbarHostState.currentSnackbarData?.dismiss() },
						showMessage = { message ->
							snackbarHostState.currentSnackbarData?.dismiss()
							coroutineScope.launch {
								snackbarHostState.showSnackbar(message)
							}
						},
						modifier = Modifier
							.padding(innerPadding),
					)
				}
			}
		}
	}
}

@Composable
fun <T> LoadingResultScreen(
	modifier: Modifier = Modifier,
	loadingResult: LoadingResult<T>,
	onRefresh: () -> Unit,
	loadingScreen: @Composable () -> Unit = { LoadingScreen() },
	failureScreen: @Composable (Throwable, Boolean) -> Unit = { _, isLoading ->
		FailureScreen(isLoading, onRefresh)
	},
	content: @Composable (T, Boolean) -> Unit,
) {
	AnimatedContent(
		modifier = modifier,
		targetState = loadingResult,
		contentKey = { it::class.simpleName },
		label = "LoadingComposable",
	) { result ->
		when (result) {
			LoadingResult.Loading -> loadingScreen()
			is LoadingResult.Failure -> failureScreen(result.throwable, result.isLoading)
			is LoadingResult.Success -> content(result.value, result.isLoading)
		}
	}
}

@Composable
fun FailureScreen(
	isLoading: Boolean,
	onRetry: () -> Unit,
) {
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		Text(text = "Oh no, something went wrong!")
		Button(onClick = onRetry) {
			AnimatedVisibility(visible = isLoading) {
				CircularProgressIndicator(
					color = LocalContentColor.current,
					modifier = Modifier
						.padding(end = 8.dp)
						.size(24.dp)
				)
			}
			Text("Try again")
		}
	}
}

@Composable
fun LoadingScreen() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center,
	) {
		CircularProgressIndicator(
			modifier = Modifier.size(48.dp)
		)
	}
}
