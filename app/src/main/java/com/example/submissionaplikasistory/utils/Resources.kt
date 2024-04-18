package com.example.submissionaplikasistory.utils

sealed class Resources<T> {
    data class OnSuccess <T>(val data: T): Resources<T>()
    data class OnFailure <T>(val message: String): Resources<T>()
    class Loading<T>()  : Resources<T>()
}