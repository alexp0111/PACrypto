package com.example.pacrypto.animator

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.example.pacrypto.R

/**
 * Class that is responsible for animating pickers such as date picker & rate picker
 * */
class PickerAnimator(private val index: (String) -> Unit) {

    fun animate(
        resources: Resources,
        context: Context?,
        currencyPicker: MutableMap<ConstraintLayout, TextView>,
        pickerCircle: ConstraintLayout
    ) {
        for (mutableEntry in currencyPicker) {
            mutableEntry.key.setOnClickListener {
                setUpPickerTransition(context, resources, mutableEntry, currencyPicker, pickerCircle)
            }
        }
    }

    private fun setUpPickerTransition(
        context: Context?,
        resources: Resources,
        entry: MutableMap.MutableEntry<ConstraintLayout, TextView>,
        picker: MutableMap<ConstraintLayout, TextView>,
        cursor: ConstraintLayout
    ) {
        val anim = ObjectAnimator.ofFloat(
            cursor,
            "translationX",
            entry.key.x - (8.0f * resources.displayMetrics.density + 0.5f)
        )
        anim.doOnStart {
            picker.forEach { i ->
                i.value.setTextColor(context?.getColorStateList(R.color.picker_text_off))
                i.key.elevation = 0f
            }
        }
        anim.doOnEnd {
            picker.forEach { i ->
                if (i != entry) i.value.setTextColor(context?.getColorStateList(R.color.picker_text_off))
            }
            entry.value.setTextColor(context?.getColorStateList(R.color.picker_text_on))
            entry.key.elevation = resources.getDimension(R.dimen.cl_elevation) + 1f
            index.invoke(entry.value.text.toString())
        }
        anim.start()
    }
}
