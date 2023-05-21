package com.example.pacrypto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pacrypto.data.CoinRepository
import com.example.pacrypto.util.OhlcvsInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for ohlcvs info
 *
 * Uses channel logic: each request is just a message to channel,
 * that handle them in it's own way
 *
 * (flatMapLatest) helps to cancel previous work when new message in channel received
 * */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OhlcvsViewModel @Inject constructor(
    private val repository: CoinRepository,
) : ViewModel() {

    private val refreshTriggerChannel = Channel<Pair<String, Boolean>>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    //

    val ohlcvs = refreshTrigger.flatMapLatest { pair ->
        repository.getOhlcv(pair.first, pair.second)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun getExactOhlcvs(ticker: String, currency: String) {
        viewModelScope.launch {
            refreshTriggerChannel.send(
                Pair(
                    OhlcvsInfo.REQUEST_PREFIX + ticker + OhlcvsInfo.REQUEST_DIVIDER + currency,
                    true
                )
            )
        }
    }

    fun getExactOhlcvs(ticker: String, currency: String, shouldFetch: Boolean) {
        viewModelScope.launch {
            refreshTriggerChannel.send(
                Pair(
                    OhlcvsInfo.REQUEST_PREFIX + ticker + OhlcvsInfo.REQUEST_DIVIDER + currency,
                    shouldFetch
                )
            )
        }
    }
}