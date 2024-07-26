package com.klitsie.dataloading.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.klitsie.dataloading.RefreshTrigger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ExampleEvent {

	data object ShowRefreshFailure : ExampleEvent

}

class ExampleViewModel(
	exampleDataLoader: ExampleDataLoader = DefaultExampleDataLoader(),
	private val exampleDataMapper: ExampleDataMapper = ExampleDataMapper,
	private val refreshTrigger: RefreshTrigger = RefreshTrigger(),
) : ViewModel() {

	private val _event = MutableStateFlow<ExampleEvent?>(null)
	val event = _event.asStateFlow()

	private val data = exampleDataLoader.loadAndObserveData(
		coroutineScope = viewModelScope,
		refreshTrigger = refreshTrigger,
		onRefreshFailure = { throwable ->
			println(throwable)
			_event.update { ExampleEvent.ShowRefreshFailure }
		},
	)

	val screenState = data.map { exampleDataMapper.map(it) }.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(),
		initialValue = exampleDataMapper.map(data.value),
	)

	fun refresh() {
		viewModelScope.launch {
			refreshTrigger.refresh()
		}
	}

	fun consumeEvent() {
		_event.update { null }
	}

}