package com.klitsie.dataloading.usecase

import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.random.Random
import kotlin.random.nextInt

fun interface FetchIntFromMemoryUseCase {

	fun fetchInt(): Result<Int>

	companion object : FetchIntFromMemoryUseCase {

		override fun fetchInt(): Result<Int> {
			return if (Random.nextBoolean()) {
				success(Random.nextInt(0..1000))
			} else {
				failure(Exception("Failed to fetch int"))
			}
		}
	}

}