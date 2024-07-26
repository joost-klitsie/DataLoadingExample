package com.klitsie.dataloading

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

/**
 * A data loader that can be used to load and observe data.
 */
sealed interface DataLoader<T> {

	/**
	 * Returns a StateFlow containing a LoadingResult<T> object. Every invocation to this function will return a new StateFlow.
	 * The data loader will automatically refresh the data at the start if initialData.isLoading() returns true, or when
	 * the [refreshTrigger] is triggered.
	 *
	 * This function will also handle the case where the refresh of data fails. If the previous data was successfully loaded,
	 * that data will be kept and the onRefreshFailure callback will be called instead. Note that the onRefreshFailure callback
	 * is not called when the previous state was not successful, as in that case the result state will contain the failure.
	 *
	 * @param coroutineScope The coroutine scope to use for the data loading and observing.
	 * @param refreshTrigger The trigger that can be used to refresh the data.
	 * @param initialData The initial data to use. If initialData.isLoading() returns true, fetchData will be called.
	 * @param observeData The result of this will be observed when the data is successfully loaded.
	 * @param onRefreshFailure This will be called when a refresh of data fails, only when the previous data was successfully loaded.
	 * @param fetchData The function to fetch the data.
	 *
	 * @return The state flow that will emit the data.
	 */
	fun loadAndObserveDataAsState(
		coroutineScope: CoroutineScope,
		refreshTrigger: RefreshTrigger? = null,
		initialData: LoadingResult<T> = loading(),
		observeData: (T) -> Flow<T> = { emptyFlow() },
		fetchData: suspend (LoadingResult<T>) -> Result<T>,
		onRefreshFailure: (Throwable) -> Unit,
	): StateFlow<LoadingResult<T>> = loadAndObserveData(
		refreshTrigger = refreshTrigger,
		initialData = initialData,
		observeData = observeData,
		fetchData = fetchData,
		onRefreshFailure = onRefreshFailure,
	).stateIn(
		scope = coroutineScope,
		started = SharingStarted.WhileSubscribed(),
		initialValue = initialData,
	)

	/**
	 * Returns a StateFlow containing a LoadingResult<T> object. Every invocation to this function will return a new StateFlow.
	 * The data loader will automatically refresh the data at the start if initialData.isLoading() returns true, or when
	 * the [refreshTrigger] is triggered.
	 *
	 * This function will also handle the case where the refresh of data fails. If the previous data was successfully loaded,
	 * that data will be kept and the onRefreshFailure callback will be called instead. Note that the onRefreshFailure callback
	 * is not called when the previous state was not successful, as in that case the result state will contain the failure.
	 *
	 * @param refreshTrigger The trigger that can be used to refresh the data.
	 * @param initialData The initial data to use. If initialData.isLoading() returns true, fetchData will be called.
	 * @param observeData The result of this will be observed when the data is successfully loaded.
	 * @param onRefreshFailure This will be called when a refresh of data fails, only when the previous data was successfully loaded.
	 * @param fetchData The function to fetch the data.
	 *
	 * @return The flow that will emit the data.
	 */
	fun loadAndObserveData(
		refreshTrigger: RefreshTrigger? = null,
		initialData: LoadingResult<T> = loading(),
		observeData: (T) -> Flow<T> = { emptyFlow() },
		fetchData: suspend (LoadingResult<T>) -> Result<T>,
		onRefreshFailure: (Throwable) -> Unit,
	): Flow<LoadingResult<T>> = loadAndObserveData(
		refreshTrigger = refreshTrigger,
		initialData = initialData,
		observeData = observeData,
		fetchData = { oldValue: LoadingResult<T> ->
			// Try to reuse old value if the new value is a failure.
			fetchData(oldValue).fold(
				onSuccess = { success(it) },
				onFailure = { exception ->
					if (oldValue is LoadingResult.Success) {
						// If we successfully recover the old value, we call the onRefreshFailure callback
						onRefreshFailure(exception)
						success(oldValue.value)
					} else {
						failure(exception)
					}
				}
			)
		},
	)

	/**
	 * Returns a StateFlow containing a LoadingResult<T> object. Every invocation to this function will return a new StateFlow.
	 * The data loader will automatically refresh the data at the start if initialData.isLoading() returns true, or when
	 * the [refreshTrigger] is triggered.
	 *
	 * @param coroutineScope The coroutine scope to use for the data loading and observing.
	 * @param refreshTrigger The trigger that can be used to refresh the data.
	 * @param initialData The initial data to use. If initialData.isLoading() returns true, fetchData will be called.
	 * @param observeData The result of this will be observed when the data is successfully loaded.
	 * @param fetchData The function to fetch the data.
	 *
	 * @return The state flow that will emit the data.
	 */
	fun loadAndObserveDataAsState(
		coroutineScope: CoroutineScope,
		refreshTrigger: RefreshTrigger? = null,
		initialData: LoadingResult<T> = loading(),
		observeData: (T) -> Flow<T> = { emptyFlow() },
		fetchData: suspend (LoadingResult<T>) -> Result<T>,
	): StateFlow<LoadingResult<T>> = loadAndObserveData(
		refreshTrigger = refreshTrigger,
		initialData = initialData,
		observeData = observeData,
		fetchData = fetchData,
	).stateIn(
		scope = coroutineScope,
		started = SharingStarted.WhileSubscribed(),
		initialValue = initialData,
	)

	/**
	 * Returns a StateFlow containing a LoadingResult<T> object. Every invocation to this function will return a new StateFlow.
	 * The data loader will automatically refresh the data at the start if initialData.isLoading() returns true, or when
	 * the [refreshTrigger] is triggered.
	 *
	 * @param refreshTrigger The trigger that can be used to refresh the data.
	 * @param initialData The initial data to use. If initialData.isLoading() returns true, fetchData will be called.
	 * @param observeData The result of this will be observed when the data is successfully loaded.
	 * @param fetchData The function to fetch the data.
	 *
	 * @return The flow that will emit the data.
	 */
	fun loadAndObserveData(
		refreshTrigger: RefreshTrigger? = null,
		initialData: LoadingResult<T> = loading(),
		observeData: (T) -> Flow<T> = { emptyFlow() },
		fetchData: suspend (LoadingResult<T>) -> Result<T>,
	): Flow<LoadingResult<T>>

}

fun <T> DataLoader(): DataLoader<T> = DefaultDataLoader()

sealed interface RefreshTrigger {

	suspend fun refresh()

}

fun RefreshTrigger(): RefreshTrigger = DefaultRefreshTrigger()

private class DefaultDataLoader<T> : DataLoader<T> {

	override fun loadAndObserveData(
		refreshTrigger: RefreshTrigger?,
		initialData: LoadingResult<T>,
		observeData: (T) -> Flow<T>,
		fetchData: suspend (LoadingResult<T>) -> Result<T>,
	): Flow<LoadingResult<T>> {
		val refreshEventFlow =
			(refreshTrigger as? DefaultRefreshTrigger)?.refreshEvent ?: emptyFlow()

		// We store the latest emitted value in the lastValue
		var lastValue = initialData

		// Emit the initial data and every time the refresh event triggers, we map the last value to a loading state
		return flow {
			emit(lastValue)
			refreshEventFlow.collect {
				// Make sure we do not emit if we are already in a loading state
				if (!lastValue.isLoading) {
					emit(lastValue.toLoading())
				}
			}
		}
			.flatMapLatest { currentResult ->
				loadAndObserveData(
					currentResult,
					observeData,
					fetchData
				)
			}
			.distinctUntilChanged()
			.onEach {
				lastValue = it
			}
	}

	private fun loadAndObserveData(
		currentResult: LoadingResult<T>,
		observeData: (T) -> Flow<T>,
		fetchData: suspend (LoadingResult<T>) -> Result<T>,
	) = flow {
		// Little helper method to observe the data and map it to a LoadingResult
		val observe: (T) -> Flow<LoadingResult<T>> =
			{ value -> observeData(value).map(::loadingSuccess) }
		// Whatever happens, emit the current result
		emit(currentResult)
		when {
			// If the current result is loading, we fetch the data and emit the result
			currentResult.isLoading -> {
				val newResult = fetchData(currentResult)
				emit(newResult.toLoadingResult())
				// If the fetching is successful, we observe the data and emit it
				newResult.onSuccess { value -> emitAll(observe(value)) }
			}

			// If the current result is successful, we simply observe and emit the data changes
			currentResult is LoadingResult.Success -> emitAll(observe(currentResult.value))
			else -> {
				// Nothing to do in case of failure and not loading
			}
		}
	}

}

private class DefaultRefreshTrigger : RefreshTrigger {

	private val _refreshEvent = MutableSharedFlow<Unit>()
	val refreshEvent = _refreshEvent.asSharedFlow()

	override suspend fun refresh() {
		_refreshEvent.emit(Unit)
	}

}
