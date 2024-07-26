package com.klitsie.dataloading.usecase

import kotlinx.coroutines.delay
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

fun interface FetchIntUseCase {

	suspend fun fetchInt(): Result<Int>

	companion object : FetchIntUseCase {

		override suspend fun fetchInt(): Result<Int> {
			delay(Random.nextInt(200..2000).milliseconds)
			return if (Random.nextBoolean()) {
				success(Random.nextInt(0..1000))
			} else {
				failure(Exception("Failed to fetch int"))
			}
		}
	}

}