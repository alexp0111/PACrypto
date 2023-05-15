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
import com.db.williamchart.ExperimentalFeature
import com.example.pacrypto.R
import com.example.pacrypto.animator.PickerAnimator
import com.example.pacrypto.data.room.ohlcvs.DBOhlcvsItem
import com.example.pacrypto.databinding.FragmentInfoBinding
import com.example.pacrypto.util.UiState
import com.example.pacrypto.viewmodel.OhlcvsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Collections

private const val TAG = "INFO_FRAGMENT"

@AndroidEntryPoint
class InfoFragment : Fragment(R.layout.fragment_info) {

    private val viewModel: OhlcvsViewModel by viewModels()
    private var dataList: List<List<DBOhlcvsItem>>? = null

    private var fragmentInfoBinding: FragmentInfoBinding? = null
    private var currencyPicker = mutableMapOf<ConstraintLayout, TextView>()
    private var datePicker = mutableMapOf<ConstraintLayout, TextView>()

    private var name: String = ""
    private var ticker: String = ""
    private var currency: String = "$"

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

        // Picker rate
        binding.apply {
            currencyPicker[currencyPicker1] = currencyPickerText1
            currencyPicker[currencyPicker2] = currencyPickerText2

            PickerAnimator {
                if (it == "$") {
                    viewModel.getExactOhlcvs(ticker, "USD")
                } else {
                    viewModel.getExactOhlcvs(ticker, "RUB")
                }
                currency = it
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
                    val currentList = when (it) {
                        "24h" -> dataList!![0].asPairedList()
                        "7d" -> dataList!![1].asPairedList()
                        "1m" -> dataList!![2].asPairedList()
                        "3m" -> dataList!![3].asPairedList()
                        "1y" -> dataList!![4].asPairedList()
                        "All" -> dataList!![5].asPairedList()
                        else -> dataList!![0].asPairedList()
                    }
                    binding.lineChart.animate(currentList.asReversed())
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
                                binding.lineChart.visibility = View.VISIBLE
                                binding.picker1.performClick()
                            }
                            binding.pb.visibility = View.GONE
                        }
                        is UiState.Failure -> {
                            if (it.data != null) {
                                dataList = it.data.periods
                                binding.lineChart.visibility = View.VISIBLE
                                binding.picker1.performClick()
                            }
                            Toast.makeText(
                                requireContext(),
                                "Сохраненные данные",
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
