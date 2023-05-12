package com.example.pacrypto.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchSuccess: () -> Unit = { },
    crossinline onFetchFailed: (Throwable) -> Unit = { }
) = channelFlow {
    val data = query().first()

    if (shouldFetch(data)) {
        val loading = launch {
            query().collect { send(UiState.Loading) }
        }

        try {
            saveFetchResult(fetch())
            onFetchSuccess()
            loading.cancel()
            query().collect { send(UiState.Success(it)) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            loading.cancel()
            query().collect { send(UiState.Failure(throwable.localizedMessage)) }
        }
    } else {
        query().collect { send(UiState.Success(it)) }
    }
}