package com.klitsie.dataloading.example

import com.klitsie.dataloading.LoadingResult
import com.klitsie.dataloading.map

interface ExampleDataMapper {

	fun map(data: LoadingResult<Int>): LoadingResult<String>

	companion object : ExampleDataMapper {

		override fun map(data: LoadingResult<Int>) = data.map {
			"The current number is $it!"
		}

	}

}
