package com.example.pacrypto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pacrypto.data.CoinRepository
import com.example.pacrypto.util.Refresh
import com.example.pacrypto.util.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


private const val TAG = "OHLCVS_VIEW_MODEL"

@HiltViewModel
class OhlcvsViewModel @Inject constructor(
    val repository: CoinRepository
) : ViewModel() {

    private val refreshTriggerChannel = Channel<String>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    //

    val ohlcvs = refreshTrigger.flatMapLatest { id ->
        repository.getOhlcv(id)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun getExactOhlcvs(ticker: String, currency: String) {
        viewModelScope.launch {
            refreshTriggerChannel.send("BITSTAMP_SPOT_" + ticker + "_" + currency)
        }
    }
}