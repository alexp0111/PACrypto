package com.example.pacrypto.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
import com.example.pacrypto.adapters.CoinAdapter
import com.example.pacrypto.animator.PickerAnimator
import com.example.pacrypto.animator.SwipeGesture
import com.example.pacrypto.data.SearchItem
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

    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private var currencyPicker = mutableMapOf<ConstraintLayout, TextView>()
    private var fabAnimJob: Job? = null

    private var fabState = FabState.HIDE
    private var searchType = SearchType.TICKER
    private var searchRate = SearchRate.USD

    val adapterType1 by lazy {
        CoinAdapter(
            requireContext(),
            onItemClicked = { pos, item ->
                val fragment = InfoFragment()
                val bundle = Bundle()
                bundle.putString("ticker", item.ticker)
                bundle.putString("name", item.name)
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction().addToBackStack(null)
                    .replace(R.id.container_main, fragment).commit()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
                    adapterType1.setRateMarker("$")
                } else {
                    searchRate = SearchRate.RUB
                    adapterType1.setRateMarker("₽")
                }
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


        // fab
        binding.fabRefresh.setOnClickListener {
            viewModel.refreshAllData()
        }


        // Adapter
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.rv.layoutManager = manager
        binding.rv.adapter = adapterType1

        val swipeGesture = object : SwipeGesture(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT) {
                    val tickerToDelete =
                        searchItemList.get(viewHolder.absoluteAdapterPosition).ticker
                    removeItemFromSP(requireActivity(), tickerToDelete)
                    adapterType1.deleteItem(viewHolder.absoluteAdapterPosition)

                    Snackbar.make(requireView(), "Убрано из закладок", Snackbar.LENGTH_LONG)
                        .setAction("Восстановить") {
                            addItemToSP(requireActivity(), tickerToDelete)
                            viewModel.getFavouriteList(getAllItemsInSP(requireActivity()))
                        }.show()
                }
            }
        }
        val touchHelper = ItemTouchHelper(swipeGesture)
        touchHelper.attachToRecyclerView(binding.rv)


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

                    touchHelper.attachToRecyclerView(binding.rv)

                    viewModel.getFavouriteList(getAllItemsInSP(requireActivity()))
                } else {
                    fabState = FabState.SHOW
                    fabRefresh.show()
                    tvHeader.text = "По запросу"

                    tvName.visibility = View.VISIBLE
                    tvTicker.visibility = View.VISIBLE
                    ivQr.visibility = View.GONE
                    ivSub.visibility = View.GONE

                    touchHelper.attachToRecyclerView(null)

                    viewModel.getExactSearchItem(etSearch.text.toString(), searchType)
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
        viewModel.getFavouriteList(getAllItemsInSP(requireActivity()))
        viewModel.refreshAllData()
    }

    private fun observers(binding: FragmentHomeBinding) {
        // Favourites
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favs.collect {
                    if (it is UiState.Success && it.data != null) {
                        searchItemList = it.data as ArrayList<SearchItem>
                        if (searchRate == SearchRate.USD) {
                            adapterType1.setRateMarker("$")
                        } else {
                            adapterType1.setRateMarker("₽")
                        }
                        adapterType1.updateList(searchItemList)
                    }
                    if (it is UiState.Failure) {
                        Toast.makeText(
                            requireContext(),
                            "Не удалось совершить поиск",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // All assets
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchItems.collect {
                    when (it) {
                        is UiState.Loading -> showLoadingInfo(binding)
                        is UiState.Success -> showSuccessInfo(binding)
                        is UiState.Failure -> showFailureInfo(binding)
                        else -> {}
                    }
                }
            }
        }

        // Search results
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.exactSearchItem.collect {
                    if (it is UiState.Success && it.data != null) {
                        searchItemList = it.data as ArrayList<SearchItem>
                        if (searchRate == SearchRate.USD) {
                            adapterType1.setRateMarker("$")
                        } else {
                            adapterType1.setRateMarker("₽")
                        }
                        adapterType1.updateList(searchItemList)
                    }
                    if (it is UiState.Failure) {
                        Toast.makeText(
                            requireContext(),
                            "Не удалось совершить поиск",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun showLoadingInfo(binding: FragmentHomeBinding) {
        if (binding.etSearch.text.isNullOrEmpty()) {
            binding.etSearch.hint = "Обновляем данные"
        }
        binding.etSearch.isEnabled = false
        binding.fabRefresh.isEnabled = false
        binding.pb.visibility = View.VISIBLE
    }

    private fun showSuccessInfo(binding: FragmentHomeBinding) {
        if (binding.etSearch.text.isNullOrEmpty()) {
            binding.etSearch.hint = "Найти..."
        }
        binding.etSearch.isEnabled = true
        binding.fabRefresh.isEnabled = true
        binding.pb.visibility = View.GONE
    }

    private fun showFailureInfo(binding: FragmentHomeBinding) {
        if (binding.etSearch.text.isNullOrEmpty()) {
            binding.etSearch.hint = "Найти..."
        }
        binding.etSearch.isEnabled = true
        binding.fabRefresh.isEnabled = true

        binding.pb.visibility = View.GONE

        Toast.makeText(
            requireContext(),
            "Данные не удалось обновить",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroy() {
        fragmentHomeBinding = null
        super.onDestroy()
    }
}
