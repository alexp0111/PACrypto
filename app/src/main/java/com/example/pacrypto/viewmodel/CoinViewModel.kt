package com.example.pacrypto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pacrypto.data.CoinRepository
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

private const val TAG = "COIN_VIEW_MODEL"

@HiltViewModel
class CoinViewModel @Inject constructor(
    val repository: CoinRepository
) : ViewModel() {

    private val refreshTriggerChannelForSearchItems = Channel<Pair<String, String>>()
    private val refreshTriggerForSearchItems =
        refreshTriggerChannelForSearchItems.receiveAsFlow()

    private val refreshTriggerChannelForExactSearchItem = Channel<Pair<String, SearchType>>()
    private val refreshTriggerForExactSearchItem =
        refreshTriggerChannelForExactSearchItem.receiveAsFlow()

    //

    val searchItems = refreshTriggerForSearchItems.flatMapLatest { pair ->
        repository.getSearchItems(pair.first, pair.second)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val exactSearchItem = refreshTriggerForExactSearchItem.flatMapLatest { pair ->
        repository.getExactSearchItem(pair)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    //


    fun refreshAllData() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
            val c = Calendar.getInstance()
            val timeActual = c.time
            c.add(Calendar.DATE, -1)
            val timePrevious = c.time
            refreshTriggerChannelForSearchItems.send(
                Pair(
                    sdf.format(timeActual),
                    sdf.format(timePrevious)
                )
            )
        }
    }

    fun getExactSearchItem(input: String, type: SearchType) {
        viewModelScope.launch {
            refreshTriggerChannelForExactSearchItem.send(Pair(input, type))
        }
    }
}