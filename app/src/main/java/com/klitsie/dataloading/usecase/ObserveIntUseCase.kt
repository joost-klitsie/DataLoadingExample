package com.klitsie.dataloading.usecase

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Duration.Companion.seconds

fun interface ObserveIntUseCase {

	fun observeInt(): Flow<Int>

	companion object : ObserveIntUseCase {

		override fun observeInt() = flow {
			while(currentCoroutineContext().isActive) {
				delay(Random.nextInt(5..20).seconds)
				emit(Random.nextInt(0..1000))
			}
		}
	}

}