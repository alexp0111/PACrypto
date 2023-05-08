package com.example.pacrypto

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.db.williamchart.Tooltip
import com.example.pacrypto.databinding.FragmentInfoBinding

private const val TAG = "INFO_FRAGMENT"

class InfoFragment : Fragment(R.layout.fragment_info) {


    private var fragmentInfoBinding: FragmentInfoBinding? = null
    private var currencyPicker = mutableMapOf<ConstraintLayout, TextView>()
    private var datePicker = mutableMapOf<ConstraintLayout, TextView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentInfoBinding.bind(view)
        fragmentInfoBinding = binding

        binding.apply {
            currencyPicker[currencyPicker1] = currencyPickerText1
            currencyPicker[currencyPicker2] = currencyPickerText2
        }

        PickerAnimator {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }.animate(resources, context, currencyPicker, binding.pickerCurrencyCircle)

        binding.apply {
            datePicker[picker1] = pickerText1
            datePicker[picker2] = pickerText2
            datePicker[picker3] = pickerText3
            datePicker[picker4] = pickerText4
            datePicker[picker5] = pickerText5
            datePicker[picker6] = pickerText6
        }

        PickerAnimator {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }.animate(resources, context, datePicker, binding.pickerCircle)

        binding.apply {
            ivBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            lineChart.gradientFillColors =
                intArrayOf(
                    resources.getColor(R.color.main_text_color),
                    Color.TRANSPARENT
                )
            lineChart.animation.duration = animationDuration
            lineChart.onDataPointTouchListener = { index, _, _ ->
                lineChartValue.text =
                    lineSet.toList()[index]
                        .toString()
            }
            lineChart.animate(lineSet)
        }
    }

    override fun onDestroy() {
        fragmentInfoBinding = null
        super.onDestroy()
    }

    companion object {
        private val lineSet = listOf(
            "label1" to 5f,
            "label2" to 4.5f,
            "label3" to 4.7f,
            "label4" to 3.5f,
            "label5" to 3.6f,
            "label6" to 7.5f,
            "label7" to 7.5f,
            "label8" to 10f,
            "label9" to 5f,
            "label10" to 6.5f,
            "label11" to 3f,
            "label12" to 4f
        )

        private const val animationDuration = 1000L
    }
}