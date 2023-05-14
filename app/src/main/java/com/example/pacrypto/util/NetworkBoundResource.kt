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
    Log.d("NBR", "1")


    if (shouldFetch(data)) {
        Log.d("NBR", "2")
        val loading = launch {
            query().collect { send(UiState.Loading) }
        }

        try {
            saveFetchResult(fetch())
            Log.d("NBR", "3")
            loading.cancel()
            Log.d("NBR", "4")
            query().collect { send(UiState.Success(it)) }
            Log.d("NBR", "5")
        } catch (throwable: Throwable) {
            loading.cancel()
            Log.d("NBR", "6")
            query().collect { send(UiState.Failure(it, "Ошибка подключения")) }
            Log.d("NBR", "7")
        }
    } else {
        Log.d("NBR", "8")
        query().collect { send(UiState.Success(it)) }
    }
}