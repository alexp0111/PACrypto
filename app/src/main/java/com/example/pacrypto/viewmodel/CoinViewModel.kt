package com.example.pacrypto.viewmodel

import androidx.lifecycle.*
import com.example.pacrypto.data.CoinRepository
import com.example.pacrypto.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    val repository: CoinRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger = refreshTriggerChannel.receiveAsFlow()

    val assets = refreshTrigger.flatMapLatest { refresh ->
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

    fun onStart() {
        if (assets.value !is UiState.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.NORMAL)
            }
        }
    }

    fun updateAssets() {
        if (assets.value !is UiState.Loading) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.FORCE)
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