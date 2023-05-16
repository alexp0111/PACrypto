package com.example.pacrypto.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.db.williamchart.ExperimentalFeature
import com.example.pacrypto.R
import com.example.pacrypto.adapters.InfoAdapter
import com.example.pacrypto.animator.PickerAnimator
import com.example.pacrypto.data.room.ohlcvs.DBOhlcvsItem
import com.example.pacrypto.databinding.FragmentInfoBinding
import com.example.pacrypto.util.*
import com.example.pacrypto.viewmodel.OhlcvsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val TAG = "INFO_FRAGMENT"

@AndroidEntryPoint
class InfoFragment : Fragment(R.layout.fragment_info) {

    private val viewModel: OhlcvsViewModel by viewModels()
    private var dataList: List<List<DBOhlcvsItem>>? = null
    private var infoList: List<DBOhlcvsItem>? = null

    private var fragmentInfoBinding: FragmentInfoBinding? = null
    private var currencyPicker = mutableMapOf<ConstraintLayout, TextView>()
    private var datePicker = mutableMapOf<ConstraintLayout, TextView>()

    private var name: String = ""
    private var ticker: String = ""

    private var currency: String = "$"

    val adapter by lazy {
        InfoAdapter(requireContext())
    }

    @OptIn(ExperimentalFeature::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentInfoBinding.bind(view)
        fragmentInfoBinding = binding

        //set up observers
        observers(binding)

        // Arguments
        name = arguments?.getString("name") ?: ""
        ticker = arguments?.getString("ticker") ?: ""

        binding.tvHeader.text = name


        // Check if favourite
        if (isItemInSP(requireActivity(), ticker)) {
            setFavouriteVisibility(true, binding)
        } else {
            setFavouriteVisibility(false, binding)
        }

        // Picker rate
        binding.apply {
            currencyPicker[currencyPicker1] = currencyPickerText1
            currencyPicker[currencyPicker2] = currencyPickerText2

            PickerAnimator {
                if (it == "$") {
                    viewModel.getExactOhlcvs(ticker, "USD", false)
                } else {
                    viewModel.getExactOhlcvs(ticker, "RUB", false)
                }
            }.animate(resources, context, currencyPicker, pickerCurrencyCircle)
        }


        // Picker date
        binding.apply {
            datePicker[picker1] = pickerText1
            datePicker[picker2] = pickerText2
            datePicker[picker3] = pickerText3
            datePicker[picker4] = pickerText4
            datePicker[picker5] = pickerText5
            datePicker[picker6] = pickerText6

            PickerAnimator {
                if (dataList != null) {
                    val i = when (it) {
                        "24h" -> 0
                        "7d" -> 1
                        "1m" -> 2
                        "3m" -> 3
                        "1y" -> 4
                        "All" -> 5
                        else -> 0
                    }

                    val currentList = dataList!![i].asPairedList()
                    binding.lineChart.animate(currentList.asReversed())
                    adapter.updateList(infoList!![i].asDoubleList(), currency)

                    lineChart.onDataPointTouchListener = { index, _, _ ->
                        val item = currentList.asReversed().toList()[index]
                        lineChartValue.text = buildString {
                            append(item.first.substring(0, 10))
                            append("||")
                            append(item.first.substring(11, 19))
                            append(": ")
                            append(item.second)
                            append(currency)
                        }

                    }
                }
            }.animate(resources, context, datePicker, pickerCircle)
        }


        // Back button
        binding.apply {
            ivBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }


        // chart
        binding.apply {
            lineChart.gradientFillColors =
                intArrayOf(
                    resources.getColor(R.color.main_text_color),
                    Color.TRANSPARENT
                )
            lineChart.animation.duration = animationDuration
        }


        // recycler
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.rv.layoutManager = manager
        binding.rv.adapter = adapter
        binding.rv.isNestedScrollingEnabled = false

        binding.ivFavFalse.setOnClickListener {
            try {
                addItemToSP(requireActivity(), ticker)
                setFavouriteVisibility(true, binding)
            } catch (e: java.lang.Exception) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivFavTrue.setOnClickListener {
            try {
                removeItemFromSP(requireActivity(), ticker)
                setFavouriteVisibility(false, binding)
            } catch (e: java.lang.Exception) {
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setFavouriteVisibility(isFav: Boolean, binding: FragmentInfoBinding) {
        if (isFav) {
            binding.ivFavTrue.visibility = View.VISIBLE
            binding.ivFavFalse.visibility = View.GONE
        } else {
            binding.ivFavTrue.visibility = View.GONE
            binding.ivFavFalse.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.getExactOhlcvs(ticker, "USD")
    }

    private fun observers(binding: FragmentInfoBinding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ohlcvs.collect {
                    when (it) {
                        is UiState.Loading -> {
                            binding.pb.visibility = View.VISIBLE
                            binding.lineChart.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            if (it.data != null) {
                                dataList = it.data.periods
                                infoList = getInfo(it.data.periods)
                                binding.lineChart.visibility = View.VISIBLE
                                binding.picker1.performClick()
                                currency = if (it.data.type.endsWith("USD")) "$" else "₽"
                                adapter.updateList(infoList!![0].asDoubleList(), currency)
                            }
                            binding.pb.visibility = View.GONE
                        }
                        is UiState.Failure -> {
                            if (it.data != null) {
                                dataList = it.data.periods
                                infoList = getInfo(it.data.periods)
                                binding.lineChart.visibility = View.VISIBLE
                                binding.picker1.performClick()
                                currency = if (it.data.type.endsWith("USD")) "$" else "₽"
                                adapter.updateList(infoList!![0].asDoubleList(), currency)
                            }
                            Toast.makeText(
                                requireContext(),
                                "Не удалось загрузить данные",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.pb.visibility = View.GONE
                        }
                        else -> {
                            Log.d(TAG, "Error")
                            binding.pb.visibility = View.GONE
                            binding.lineChart.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun getInfo(dataList: List<List<DBOhlcvsItem>>): List<DBOhlcvsItem> {
        val list = mutableListOf<DBOhlcvsItem>()
        dataList.forEach { extList ->
            list.add(
                DBOhlcvsItem(
                    price_close = extList.first().price_close,
                    price_high = extList.maxWith(Comparator.comparingDouble { it.price_high }).price_high,
                    price_low = extList.minWith(Comparator.comparingDouble { it.price_low }).price_low,
                    price_open = extList.last().price_open,
                    time_close = extList.first().time_close,
                    time_open = extList.last().time_open,
                    time_period_end = extList.first().time_period_end,
                    time_period_start = extList.last().time_period_start,
                    trades_count = extList.sumOf { it.trades_count },
                    volume_traded = extList.sumOf { it.volume_traded }
                )
            )
        }
        return list
    }

    override fun onDestroy() {
        fragmentInfoBinding = null
        super.onDestroy()
    }

    companion object {
        private const val animationDuration = 1000L
    }
}

private fun List<DBOhlcvsItem>.asPairedList(): List<Pair<String, Float>> {
    val list = mutableListOf<Pair<String, Float>>()
    this.forEach {
        list.add(Pair(it.time_open, it.price_open.toFloat()))
    }
    return list
}
