package com.example.pacrypto.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.example.pacrypto.util.*
import com.example.pacrypto.viewmodel.CoinViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


private const val TAG = "HOME_FRAGMENT"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: CoinViewModel by viewModels()

    private var searchItemList = arrayListOf<SearchItem>()
    private var ratesUSDAct = listOf<Rate>()
    private var ratesRUBAct = listOf<Rate>()
    private var ratesUSDPrv = listOf<Rate>()
    private var ratesRUBPrv = listOf<Rate>()

    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private var currencyPicker = mutableMapOf<ConstraintLayout, TextView>()
    private var fabAnimJob: Job? = null

    private var fabState = FabState.HIDE
    private var searchType = SearchType.TICKER
    private var searchRate = SearchRate.USD

    private var loading = 0

    private var extendedAdapter = true

    val adapterType1 by lazy {
        CurrencyAdapterType1(
            requireContext(),
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
                    searchItemList.addRates(ratesUSDAct)
                    adapterType1.setRateMarker("$")
                } else {
                    searchRate = SearchRate.RUB
                    searchItemList.addRates(ratesRUBAct)
                    adapterType1.setRateMarker("₽")
                }
                viewModel.getExactAsset(etSearch.text.toString(), searchType)
            }.animate(resources, context, currencyPicker, pickerCircle)
        }


        // searchType picker
        binding.apply {
            tvName.setOnClickListener {
                searchType = SearchType.NAME
                tvName.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.picker_text_on
                    )
                )
                tvTicker.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.picker_text_off
                    )
                )
            }

            tvTicker.setOnClickListener {
                searchType = SearchType.TICKER
                tvTicker.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.picker_text_on
                    )
                )
                tvName.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.picker_text_off
                    )
                )
            }
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

                    tvName.visibility = View.GONE
                    tvTicker.visibility = View.GONE
                    ivQr.visibility = View.VISIBLE
                    ivSub.visibility = View.VISIBLE

                    //TODO: set up bookmarks
                } else {
                    fabState = FabState.SHOW
                    fabRefresh.show()
                    tvHeader.text = "По запросу"

                    tvName.visibility = View.VISIBLE
                    tvTicker.visibility = View.VISIBLE
                    ivQr.visibility = View.GONE
                    ivSub.visibility = View.GONE

                    viewModel.getExactAsset(etSearch.text.toString(), searchType)
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
            viewModel.allAssets.collect {
                val result = it ?: return@collect

                if (result is UiState.Loading) {
                    binding.etSearch.hint = "Обновляем данные"
                    binding.etSearch.isEnabled = false
                }

                if (result is UiState.Success) {
                    loading++
                    if (loading == 5) {
                        binding.etSearch.hint = "Найти..."
                        binding.etSearch.isEnabled = true
                    }
                } else if (result is UiState.Failure) {
                    binding.etSearch.hint = result.error.toString()
                    binding.etSearch.isEnabled = true
                }
            }
        }

        // Rates in USD (Actual)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allUSDRatesAct.collect {
                val result = it ?: return@collect

                if (result is UiState.Loading) {
                    binding.etSearch.hint = "Обновляем данные"
                    binding.etSearch.isEnabled = false
                }

                if (result is UiState.Success) {
                    ratesUSDAct = result.data.rates
                    loading++
                    if (loading == 5) {
                        binding.etSearch.hint = "Найти..."
                        binding.etSearch.isEnabled = true
                    }
                } else if (result is UiState.Failure) {
                    binding.etSearch.hint = result.error.toString()
                    binding.etSearch.isEnabled = true
                }
            }
        }

        // Rates in USD (Previous)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allUSDRatesPrv.collect {
                val result = it ?: return@collect

                if (result is UiState.Loading) {
                    binding.etSearch.hint = "Обновляем данные"
                    binding.etSearch.isEnabled = false
                }

                if (result is UiState.Success) {
                    ratesUSDPrv = result.data.rates
                    loading++
                    if (loading == 5) {
                        binding.etSearch.hint = "Найти..."
                        binding.etSearch.isEnabled = true
                    }
                } else if (result is UiState.Failure) {
                    binding.etSearch.hint = result.error.toString()
                    binding.etSearch.isEnabled = true
                }
            }
        }

        // rates in RUB (Actual)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allRUBRatesAct.collect {
                val result = it ?: return@collect

                if (result is UiState.Loading) {
                    binding.etSearch.hint = "Обновляем данные"
                    binding.etSearch.isEnabled = false
                }

                if (result is UiState.Success) {
                    ratesRUBAct = result.data.rates
                    loading++
                    if (loading == 5) {
                        binding.etSearch.hint = "Найти..."
                        binding.etSearch.isEnabled = true
                    }
                } else if (result is UiState.Failure) {
                    binding.etSearch.hint = result.error.toString()
                    binding.etSearch.isEnabled = true
                }
            }
        }

        // rates in RUB (Previous)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allRUBRatesPrv.collect {
                val result = it ?: return@collect

                if (result is UiState.Loading) {
                    binding.etSearch.hint = "Обновляем данные"
                    binding.etSearch.isEnabled = false
                }

                if (result is UiState.Success) {
                    ratesRUBPrv = result.data.rates

                    loading++
                    if (loading == 5) {
                        binding.etSearch.hint = "Найти..."
                        binding.etSearch.isEnabled = true
                    }
                } else if (result is UiState.Failure) {
                    binding.etSearch.hint = result.error.toString()
                    binding.etSearch.isEnabled = true
                }
            }
        }

        // Search results
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.exactAsset.collect {
                val result = it ?: return@collect

                if (result is UiState.Success) {
                    searchItemList.addAssets(result.data)
                    if (searchRate == SearchRate.USD) {
                        searchItemList.addRates(ratesUSDAct)
                        searchItemList.calcPercents(ratesUSDAct, ratesUSDPrv)
                        adapterType1.setRateMarker("$")
                    } else {
                        searchItemList.addRates(ratesRUBAct)
                        searchItemList.calcPercents(ratesRUBAct, ratesRUBPrv)
                        adapterType1.setRateMarker("₽")
                    }
                    adapterType1.updateList(searchItemList)
                } else if (result is UiState.Failure) {
                    // error msg
                }
            }
        }
    }

    private fun swapAdapter(binding: FragmentHomeBinding) {
        // if (extendedAdapter) {
        //     //binding.rv.adapter = adapterType2
        //     //adapterType2.updateList(getTestList())
        //     extendedAdapter = false
        // } else {
        //     binding.rv.adapter = adapterType1
        //     //adapterType1.updateList(getTestList())
        //     extendedAdapter = true
        // }
    }

    override fun onDestroy() {
        fragmentHomeBinding = null
        super.onDestroy()
    }
}
