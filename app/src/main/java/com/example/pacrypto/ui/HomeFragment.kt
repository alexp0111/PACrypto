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
import com.example.pacrypto.data.CurrencyInfo
import com.example.pacrypto.data.room.DBAsset
import com.example.pacrypto.databinding.FragmentHomeBinding
import com.example.pacrypto.util.AnimationDelays
import com.example.pacrypto.util.UiState
import com.example.pacrypto.viewmodel.CoinViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


private const val TAG = "HOME_FRAGMENT"

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: CoinViewModel by viewModels()

    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private var currencyPicker = mutableMapOf<ConstraintLayout, TextView>()
    private var fabAnimJob: Job? = null
    private var fabState = FabState.HIDE
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
        observers()


        // init picker
        binding.apply {
            currencyPicker[picker1] = pickerText1
            currencyPicker[picker2] = pickerText2

            PickerAnimator {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.assetsByTicker.collect {
                    val result = it ?: return@collect

                    if (result is UiState.Success) {
                        adapterType1.updateList(result.data as ArrayList<DBAsset>)
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

    private fun getTestList(): ArrayList<CurrencyInfo> {
        val list = arrayListOf<CurrencyInfo>()
        list.add(
            CurrencyInfo(
                "Bitcoin", "BTC", "2010-07-17", "2019-11-03", 1000.9, null
            )
        )
        list.add(
            CurrencyInfo(
                "Etherium", "ETH", "2012-07-17", "2020-11-03", 254.8, null
            )
        )
        list.add(
            CurrencyInfo(
                "Etherium", "ETH", "2012-07-17", "2020-11-03", 254.8, null
            )
        )
        list.add(
            CurrencyInfo(
                "Etherium", "ETH", "2012-07-17", "2020-11-03", 254.8, null
            )
        )
        list.add(
            CurrencyInfo(
                "Etherium", "ETH", "2012-07-17", "2020-11-03", 254.8, null
            )
        )
        list.add(
            CurrencyInfo(
                "Etherium", "ETH", "2012-07-17", "2020-11-03", 254.8, null
            )
        )
        list.add(
            CurrencyInfo(
                "Etherium", "ETH", "2012-07-17", "2020-11-03", 254.8, null
            )
        )
        list.add(
            CurrencyInfo(
                "Etherium", "ETH", "2012-07-17", "2020-11-03", 254.8, null
            )
        )
        return list
    }

    override fun onDestroy() {
        fragmentHomeBinding = null
        super.onDestroy()
    }

    enum class FabState {
        SHOW, HIDE
    }
}