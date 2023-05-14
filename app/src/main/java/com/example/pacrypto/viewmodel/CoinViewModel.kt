package com.example.pacrypto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pacrypto.data.CoinRepository
import com.example.pacrypto.util.SearchType
import com.example.pacrypto.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

private const val TAG = "COIN_VIEW_MODEL"

@HiltViewModel
class CoinViewModel @Inject constructor(
    val repository: CoinRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannelForAllAssets = Channel<Refresh>()
    private val refreshTriggerForAllAssets = refreshTriggerChannelForAllAssets.receiveAsFlow()

    private val refreshTriggerChannelForExactAssets = Channel<Pair<String, SearchType>>()
    private val refreshTriggerForExactAssets = refreshTriggerChannelForExactAssets.receiveAsFlow()

    //

    private val refreshTriggerChannelForAllUSDRatesAct = Channel<String>()
    private val refreshTriggerForAllUSDRatesAct = refreshTriggerChannelForAllUSDRatesAct.receiveAsFlow()

    private val refreshTriggerChannelForAllRUBRatesAct = Channel<String>()
    private val refreshTriggerForAllRUBRatesAct = refreshTriggerChannelForAllRUBRatesAct.receiveAsFlow()

    //

    private val refreshTriggerChannelForAllUSDRatesPrv = Channel<String>()
    private val refreshTriggerForAllUSDRatesPrv = refreshTriggerChannelForAllUSDRatesPrv.receiveAsFlow()

    private val refreshTriggerChannelForAllRUBRatesPrv = Channel<String>()
    private val refreshTriggerForAllRUBRatesPrv = refreshTriggerChannelForAllRUBRatesPrv.receiveAsFlow()

    //

    val allAssets = refreshTriggerForAllAssets.flatMapLatest { refresh ->
        repository.getAssets(
            forceRefresh = refresh == Refresh.FORCE,
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

    val allUSDRatesAct = refreshTriggerForAllUSDRatesAct.flatMapLatest { time ->
        repository.getUSDRates(
            time = time,
            actual = true,
            forceRefresh = true,
            onFetchSuccess = {
                //TODO:
            }
        ) { error ->
            viewModelScope.launch {
                eventChannel.send(Event.ShowErrorMessage(error))
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val allUSDRatesPrv = refreshTriggerForAllUSDRatesPrv.flatMapLatest { time ->
        repository.getUSDRates(
            time = time,
            actual = false,
            forceRefresh = true,
            onFetchSuccess = {
                //TODO:
            }
        ) { error ->
            viewModelScope.launch {
                eventChannel.send(Event.ShowErrorMessage(error))
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val allRUBRatesAct = refreshTriggerForAllRUBRatesAct.flatMapLatest { time ->
        repository.getRUBRates(
            time = time,
            actual = true,
            forceRefresh = true,
            onFetchSuccess = {
                //TODO:
            }
        ) { error ->
            viewModelScope.launch {
                eventChannel.send(Event.ShowErrorMessage(error))
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val allRUBRatesPrv = refreshTriggerForAllRUBRatesPrv.flatMapLatest { time ->
        repository.getRUBRates(
            time = time,
            actual = false,
            forceRefresh = true,
            onFetchSuccess = {
                //TODO:
            }
        ) { error ->
            viewModelScope.launch {
                eventChannel.send(Event.ShowErrorMessage(error))
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    val exactAsset = refreshTriggerForExactAssets.flatMapLatest { pair ->
        repository.getExactAsset(pair)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)


    fun onStart() {
        if (allAssets.value !is UiState.Loading
            && allUSDRatesAct.value !is UiState.Loading
            && allRUBRatesAct.value !is UiState.Loading
        ) {
            viewModelScope.launch {
                refreshTriggerChannelForAllAssets.send(Refresh.NORMAL)
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                val c = Calendar.getInstance()
                c.time = Calendar.getInstance().time

                refreshTriggerChannelForAllUSDRatesAct.send(
                    sdf.format(c.time)
                )
                refreshTriggerChannelForAllRUBRatesAct.send(
                    sdf.format(c.time)
                )
                c.add(Calendar.DATE, -1)
                refreshTriggerChannelForAllUSDRatesPrv.send(
                    sdf.format(c.time)
                )
                refreshTriggerChannelForAllRUBRatesPrv.send(
                    sdf.format(c.time)
                )
            }
        }
    }

    fun getExactAsset(searchTool: String, type: SearchType) {
        if (exactAsset.value !is UiState.Loading) {
            viewModelScope.launch {
                refreshTriggerChannelForExactAssets.send(Pair(searchTool, type))
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