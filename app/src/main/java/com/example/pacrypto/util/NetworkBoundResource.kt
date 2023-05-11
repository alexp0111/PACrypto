package com.example.pacrypto.util

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(UiState.Loading)

        try {
            saveFetchResult(fetch())
            query().map { UiState.Success(it) }
        } catch (throwable: Throwable) {
            query().map { UiState.Failure(throwable.localizedMessage) }
        }
    } else {
        query().map { UiState.Success(it) }
    }

    emitAll(flow)
}