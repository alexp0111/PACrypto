package com.example.pacrypto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pacrypto.data.CoinRepository
import com.example.pacrypto.ui.HomeFragment
import com.example.pacrypto.util.SearchType
import com.example.pacrypto.util.UiState
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

    private val refreshTriggerChannelForAllUSDRates = Channel<String>()
    private val refreshTriggerForAllUSDRates = refreshTriggerChannelForAllUSDRates.receiveAsFlow()

    private val refreshTriggerChannelForAllRUBRates = Channel<String>()
    private val refreshTriggerForAllRUBRates = refreshTriggerChannelForAllRUBRates.receiveAsFlow()

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

    val allUSDRates = refreshTriggerForAllUSDRates.flatMapLatest { time ->
        repository.getUSDRates(
            time = time,
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

    val allRUBRates = refreshTriggerForAllRUBRates.flatMapLatest { time ->
        repository.getRUBRates(
            time = time,
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
            && allUSDRates.value !is UiState.Loading
            && allRUBRates.value !is UiState.Loading) {
            viewModelScope.launch {
                refreshTriggerChannelForAllAssets.send(Refresh.NORMAL)
                val sdf = SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                refreshTriggerChannelForAllUSDRates.send(
                    sdf.format(Calendar.getInstance().time)
                )
                refreshTriggerChannelForAllRUBRates.send(
                    sdf.format(Calendar.getInstance().time)
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