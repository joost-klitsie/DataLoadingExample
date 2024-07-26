package com.klitsie.dataloading

sealed interface LoadingResult<out T> {
	val isLoading: Boolean

	data class Success<T>(
		val value: T,
		override val isLoading: Boolean = false,
	) : LoadingResult<T>

	data class Failure(
		val throwable: Throwable,
		override val isLoading: Boolean = false,
	) : LoadingResult<Nothing>

	data object Loading: LoadingResult<Nothing> {
		override val isLoading: Boolean = true
	}
}

fun <T> loading(): LoadingResult<T> = LoadingResult.Loading

fun <T> loadingSuccess(
	value: T,
): LoadingResult<T> = LoadingResult.Success(value)

fun <T> loadingFailure(
	throwable: Throwable,
): LoadingResult<T> = LoadingResult.Failure(throwable)

fun <T> Result<T>.toLoadingResult() = fold(
	onSuccess = { loadingSuccess(it) },
	onFailure = { loadingFailure(it) },
)

fun <T,R> LoadingResult<T>.map(
	block: (T) -> R,
): LoadingResult<R> = when(this) {
	is LoadingResult.Success -> LoadingResult.Success(block(value), isLoading)
	is LoadingResult.Failure -> LoadingResult.Failure(throwable, isLoading)
	is LoadingResult.Loading -> LoadingResult.Loading
}

fun <T> LoadingResult<T>.toLoading(): LoadingResult<T> = when(this) {
	is LoadingResult.Success -> copy(isLoading = true)
	is LoadingResult.Failure -> copy(isLoading = true)
	is LoadingResult.Loading -> LoadingResult.Loading
}
