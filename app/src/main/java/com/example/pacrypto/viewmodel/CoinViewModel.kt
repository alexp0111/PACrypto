package com.example.pacrypto.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pacrypto.data.CoinRepository
import com.example.pacrypto.util.DatePattern
import com.example.pacrypto.util.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * ViewModel for searchItem info
 *
 * Uses channel logic: each request is just a message to channel,
 * that handle them in it's own way
 *
 * (flatMapLatest) helps to cancel previous work when new message in channel received
 * */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CoinViewModel @Inject constructor(
    private val repository: CoinRepository
) : ViewModel() {

    private val refreshTriggerChannelForSearchItems = Channel<Pair<String, String>>()
    private val refreshTriggerForSearchItems =
        refreshTriggerChannelForSearchItems.receiveAsFlow()

    private val refreshTriggerChannelForExactSearchItem = Channel<Pair<String, SearchType>>()
    private val refreshTriggerForExactSearchItem =
        refreshTriggerChannelForExactSearchItem.receiveAsFlow()

    private val refreshTriggerChannelForFavs = Channel<List<String>>()
    private val refreshTriggerForFavs =
        refreshTriggerChannelForFavs.receiveAsFlow()

    //

    val searchItems = refreshTriggerForSearchItems.flatMapLatest { pair ->
        repository.getSearchItems(pair.first, pair.second)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val exactSearchItem = refreshTriggerForExactSearchItem.flatMapLatest { pair ->
        repository.getExactSearchItem(pair)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val favs = refreshTriggerForFavs.flatMapLatest { list ->
        repository.getFavs(list)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    //


    fun refreshAllData() {
        viewModelScope.launch {
            val sdf = SimpleDateFormat(DatePattern.FULL_INFO, Locale.getDefault())
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

    fun getFavouriteList(allItemsInSP: List<String>) {
        viewModelScope.launch {
            refreshTriggerChannelForFavs.send(allItemsInSP)
        }
    }
}