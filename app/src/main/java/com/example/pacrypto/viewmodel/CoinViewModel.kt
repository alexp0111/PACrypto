package com.example.pacrypto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pacrypto.data.CoinRepository
import com.example.pacrypto.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    val repository: CoinRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannelForAllAssets = Channel<Refresh>()
    private val refreshTriggerForAllAssets = refreshTriggerChannelForAllAssets.receiveAsFlow()

    private val refreshTriggerChannelForTickerAssets = Channel<String>()
    private val refreshTriggerForTickerAssets = refreshTriggerChannelForTickerAssets.receiveAsFlow()

    val allAssets = refreshTriggerForAllAssets.flatMapLatest { refresh ->
        repository.getAssets(
            refresh == Refresh.FORCE,
            onFetchSuccess = {
                //TODO:
            },
            onFetchFailed = { error ->
                viewModelScope.launch {
                    eventChannel.send(Event.ShowErrorMessage(error))
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    val assetsByTicker = refreshTriggerForTickerAssets.flatMapLatest { ticker ->
        repository.getAssetsByTicker(ticker)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun onStart() {
        if (allAssets.value !is UiState.Loading) {
            viewModelScope.launch {
                refreshTriggerChannelForAllAssets.send(Refresh.NORMAL)
            }
        }
    }

    fun getAssetByTicker(ticker: String) {
        if (assetsByTicker.value !is UiState.Loading) {
            viewModelScope.launch {
                refreshTriggerChannelForTickerAssets.send(ticker)
            }
        }
    }

    fun updateAssets() {
        if (allAssets.value !is UiState.Loading) {
            viewModelScope.launch {
                refreshTriggerChannelForAllAssets.send(Refresh.FORCE)
            }
        }
    }

    enum class Refresh {
        FORCE, NORMAL
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
    }
}