package com.example.pacrypto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pacrypto.data.CoinRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "OHLCVS_VIEW_MODEL"

@HiltViewModel
class OhlcvsViewModel @Inject constructor(
    val repository: CoinRepository
) : ViewModel() {

    private val refreshTriggerChannel = Channel<Pair<String, Boolean>>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    //

    val ohlcvs = refreshTrigger.flatMapLatest { pair ->
        repository.getOhlcv(pair.first, pair.second)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun getExactOhlcvs(ticker: String, currency: String) {
        viewModelScope.launch {
            refreshTriggerChannel.send(Pair("BITSTAMP_SPOT_" + ticker + "_" + currency, true))
        }
    }

    fun getExactOhlcvs(ticker: String, currency: String, shouldFetch: Boolean) {
        viewModelScope.launch {
            refreshTriggerChannel.send(Pair("BITSTAMP_SPOT_" + ticker + "_" + currency, shouldFetch))
        }
    }
}