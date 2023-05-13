package com.example.pacrypto.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pacrypto.R
import com.example.pacrypto.adapters.CurrencyAdapterType1
import com.example.pacrypto.adapters.CurrencyAdapterType2
import com.example.pacrypto.animator.PickerAnimator
import com.example.pacrypto.animator.SwipeGesture
import com.example.pacrypto.data.SearchItem
import com.example.pacrypto.data.room.rates.Rate
import com.example.pacrypto.databinding.FragmentHomeBinding
import com.example.pacrypto.util.AnimationDelays
import com.example.pacrypto.util.UiState
import com.example.pacrypto.util.addAssets
import com.example.pacrypto.util.addRates
import com.example.pacrypto.viewmodel.CoinViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


private const val TAG = "HOME_FRAGMENT"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: CoinViewModel by viewModels()

    private var searchItemList = arrayListOf<SearchItem>()
    private var ratesUSD = listOf<Rate>()
    private var ratesRUB = listOf<Rate>()

    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private var currencyPicker = mutableMapOf<ConstraintLayout, TextView>()
    private var fabAnimJob: Job? = null

    private var fabState = FabState.HIDE
    private var searchType = SearchType.TICKER
    private var searchRate = SearchRate.USD

    private var extendedAdapter = true

    val adapterType1 by lazy {
        CurrencyAdapterType1(
            onItemClicked = { pos, item ->
                Toast.makeText(context, item.toString(), Toast.LENGTH_SHORT).show()
            }
        )
    }

    val adapterType2 by lazy {
        CurrencyAdapterType2(
            onItemClicked = { pos, item ->
                Toast.makeText(context, item.toString(), Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        fragmentHomeBinding = binding


        //set up observers
        observers(binding)


        // init picker
        binding.apply {
            currencyPicker[picker1] = pickerText1
            currencyPicker[picker2] = pickerText2

            PickerAnimator {
                if (it == "$") {
                    searchRate = SearchRate.USD
                    searchItemList.addRates(ratesUSD)
                    adapterType1.setRateMarker("$")
                } else {
                    searchRate = SearchRate.RUB
                    searchItemList.addRates(ratesRUB)
                    adapterType1.setRateMarker("₽")
                }
                viewModel.getAssetByTicker(etSearch.text.toString())
            }.animate(resources, context, currencyPicker, pickerCircle)
        }


        // subscriptions button listener
        binding.ivSub.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_main, TestFragment())
                .addToBackStack(null).commit()
        }


        // Adapter
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.rv.layoutManager = manager
        binding.rv.adapter = adapterType1

        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {
                    if (extendedAdapter) {
                        adapterType1.deleteItem(viewHolder.absoluteAdapterPosition)
                    } else {
                        adapterType2.deleteItem(viewHolder.absoluteAdapterPosition)
                    }

                    Snackbar.make(requireView(), "Убрано из закладок", Snackbar.LENGTH_LONG)
                        .setAction("Восстановить") {
                            //TODO:
                        }.show()
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.rv)
        //adapterType1.updateList()


        // header listener
        binding.tvHeader.setOnClickListener {
            swapAdapter(binding)
        }


        // Search bar
        binding.apply {
            etSearch.addTextChangedListener {
                if (etSearch.text.isNullOrEmpty()) {
                    fabState = FabState.HIDE
                    fabRefresh.hide()
                    tvHeader.text = "Закладки"

                    //TODO: set up bookmarks
                } else {
                    fabState = FabState.SHOW
                    fabRefresh.show()
                    tvHeader.text = "По запросу"

                    viewModel.getAssetByTicker(etSearch.text.toString())
                }
            }
        }

        // Handle fab visibility while scrolling
        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && binding.fabRefresh.isShown()) {
                    fabAnimJob?.cancel()
                    binding.fabRefresh.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (fabState == FabState.SHOW && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fabAnimJob?.cancel()
                    fabAnimJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(AnimationDelays.FABDelay)
                        binding.fabRefresh.show()
                    }
                } else if (fabState == FabState.SHOW && newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    binding.fabRefresh.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    private fun observers(binding: FragmentHomeBinding) {
        // All assets
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allAssets.collect {
                    val result = it ?: return@collect

                    if (result is UiState.Loading) {
                        binding.etSearch.hint = "Обновляем данные"
                        binding.etSearch.isEnabled = false
                    }

                    if (result is UiState.Success) {
                        binding.etSearch.hint = "Найти..."
                        binding.etSearch.isEnabled = true
                    } else if (result is UiState.Failure) {
                        binding.etSearch.hint = result.error.toString()
                        binding.etSearch.isEnabled = true
                    }
                }
            }
        }

        // Rates in USD
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allUSDRates.collect {
                    val result = it ?: return@collect

                    if (result is UiState.Loading) {
                        binding.etSearch.hint = "Обновляем данные"
                        binding.etSearch.isEnabled = false
                    }

                    if (result is UiState.Success) {
                        binding.etSearch.hint = "Найти..."
                        binding.etSearch.isEnabled = true
                        ratesUSD = result.data.rates
                    } else if (result is UiState.Failure) {
                        binding.etSearch.hint = result.error.toString()
                        binding.etSearch.isEnabled = true
                    }
                }
            }
        }

        // rates in RUB
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allRUBRates.collect {
                    val result = it ?: return@collect

                    if (result is UiState.Loading) {
                        binding.etSearch.hint = "Обновляем данные"
                        binding.etSearch.isEnabled = false
                    }

                    if (result is UiState.Success) {
                        binding.etSearch.hint = "Найти..."
                        binding.etSearch.isEnabled = true
                        ratesRUB = result.data.rates
                    } else if (result is UiState.Failure) {
                        binding.etSearch.hint = result.error.toString()
                        binding.etSearch.isEnabled = true
                    }
                }
            }
        }

        // Search results
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.assetsByTicker.collect {
                    val result = it ?: return@collect

                    if (result is UiState.Success) {
                        searchItemList.addAssets(result.data)
                        if (searchRate == SearchRate.USD) {
                            searchItemList.addRates(ratesUSD)
                            adapterType1.setRateMarker("$")
                        } else {
                            searchItemList.addRates(ratesRUB)
                            adapterType1.setRateMarker("₽")
                        }
                        adapterType1.updateList(searchItemList)
                    } else if (result is UiState.Failure) {
                        // error msg
                    }
                }
            }
        }
    }

    private fun swapAdapter(binding: FragmentHomeBinding) {
        if (extendedAdapter) {
            binding.rv.adapter = adapterType2
            //adapterType2.updateList(getTestList())
            extendedAdapter = false
        } else {
            binding.rv.adapter = adapterType1
            //adapterType1.updateList(getTestList())
            extendedAdapter = true
        }
    }

    override fun onDestroy() {
        fragmentHomeBinding = null
        super.onDestroy()
    }

    enum class FabState {
        SHOW, HIDE
    }

    enum class SearchType {
        TICKER, NAME
    }

    enum class SearchRate {
        USD, RUB
    }
}
