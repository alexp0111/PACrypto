package com.example.pacrypto.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * This function provides functionality connected to offline-first logic
 *
 * First of all it gets data from database
 * if request needs new data, then we send api request (fetch) & send result to database (saveFetchResult)
 * and send data to flow from database
 *
 * otherwise we need just get data from database and send to channel
 *
 *
 * Important to note: this function send result to channel ONLY from database,
 * so - user always have access to all data in offline
 * */
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
            query().collect { send(UiState.Failure(it, Errors().CONNECTION_ERROR)) }
        }
    } else {
        query().collect { send(UiState.Success(it)) }
    }
}