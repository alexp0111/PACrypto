package com.example.pacrypto.util

/**
 * Wrapper for data that helps to handle results in ui layer
 * */
sealed class UiState<out T>(
    val data: T? = null,
    val error: String? = null
) {
    object Loading : UiState<Nothing>()
    class Success<T>(data: T) : UiState<T>(data)
    class Failure<T>(data: T, error: String?) : UiState<T> (data, error)
}