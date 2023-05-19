package com.example.pacrypto.util

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit = { },
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
) = channelFlow {
    val data = query().first()


    if (shouldFetch(data)) {
        val loading = launch {
            query().collect { send(UiState.Loading) }
        }

        try {
            saveFetchResult(fetch())
            loading.cancel()
            query().collect { send(UiState.Success(it)) }
        } catch (throwable: Throwable) {
            loading.cancel()
            query().collect { send(UiState.Failure(it, "Ошибка подключения")) }
        }
    } else {
        query().collect { send(UiState.Success(it)) }
    }
}