package com.example.submissionaplikasistory.utils

import androidx.test.espresso.idling.CountingIdlingResource

object ExpressIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    ExpressIdlingResource.increment() // Set app as busy.
    return try {
        function()
    } finally {
        ExpressIdlingResource.decrement() // Set app as idle.
    }
}