package com.klitsie.dataloading.example

import com.klitsie.dataloading.DataLoader
import com.klitsie.dataloading.usecase.FetchIntFromMemoryUseCase
import com.klitsie.dataloading.usecase.FetchIntUseCase
import com.klitsie.dataloading.LoadingResult
import com.klitsie.dataloading.usecase.ObserveIntUseCase
import com.klitsie.dataloading.RefreshTrigger
import com.klitsie.dataloading.loading
import com.klitsie.dataloading.loadingSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface ExampleDataLoader {

	fun loadAndObserveData(
		coroutineScope: CoroutineScope,
		refreshTrigger: RefreshTrigger,
		onRefreshFailure: (Throwable) -> Unit,
	): StateFlow<LoadingResult<Int>>

}

internal class DefaultExampleDataLoader(
	private val fetchIntFromMemoryUseCase: FetchIntFromMemoryUseCase = FetchIntFromMemoryUseCase,
	private val fetchIntUseCase: FetchIntUseCase = FetchIntUseCase,
	private val observeIntUseCase: ObserveIntUseCase = ObserveIntUseCase,
	private val dataLoader: DataLoader<Int> = DataLoader(),
) : ExampleDataLoader {

	override fun loadAndObserveData(
		coroutineScope: CoroutineScope,
		refreshTrigger: RefreshTrigger,
		onRefreshFailure: (Throwable) -> Unit,
	) = dataLoader.loadAndObserveDataAsState(
		coroutineScope = coroutineScope,
		refreshTrigger = refreshTrigger,
		initialData = fetchIntFromMemoryUseCase.fetchInt().fold(
			onSuccess = { loadingSuccess(it) },
			onFailure = { loading() },
		),
		observeData = { observeIntUseCase.observeInt() },
		fetchData = { fetchIntUseCase.fetchInt() },
		onRefreshFailure = onRefreshFailure,
	)

}