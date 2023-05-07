package com.example.pacrypto

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.db.williamchart.Tooltip
import com.example.pacrypto.databinding.FragmentInfoBinding

private const val TAG = "INFO_FRAGMENT"

class InfoFragment : Fragment(R.layout.fragment_info) {


    private var fragmentInfoBinding: FragmentInfoBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentInfoBinding.bind(view)
        fragmentInfoBinding = binding

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