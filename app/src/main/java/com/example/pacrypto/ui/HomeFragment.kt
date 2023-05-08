package com.example.pacrypto.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pacrypto.R
import com.example.pacrypto.adapters.CurrencyAdapterType1
import com.example.pacrypto.adapters.CurrencyAdapterType2
import com.example.pacrypto.animator.PickerAnimator
import com.example.pacrypto.data.CurrencyInfo
import com.example.pacrypto.databinding.FragmentHomeBinding

private const val TAG = "HOME_FRAGMENT"

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private var currencyPicker = mutableMapOf<ConstraintLayout, TextView>()

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

        // init picker
        binding.apply {
            currencyPicker[picker1] = pickerText1
            currencyPicker[picker2] = pickerText2
        }


        PickerAnimator {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }.animate(resources, context, currencyPicker, binding.pickerCircle)

        // subscriptions button listener
        binding.ivSub.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_main, InfoFragment())
                .addToBackStack(null).commit()
        }

        // Adapter
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL
        binding.rv.layoutManager = manager
        binding.rv.adapter = adapterType1

        adapterType1.updateList(getTestList())

        binding.tvHeader.setOnClickListener {
            if (recyclerType == 1){
                binding.rv.adapter = adapterType2
                adapterType2.updateList(getTestList())
                recyclerType = 2
            } else {
                binding.rv.adapter = adapterType1
                adapterType1.updateList(getTestList())
                recyclerType = 1
            }
        }
    }

    private fun getTestList(): List<CurrencyInfo> {
        val list = mutableListOf<CurrencyInfo>()
        list.add(CurrencyInfo(
            "Bitcoin", "BTC", "2010-07-17", "2019-11-03", 1000.9, null
        ))
        list.add(CurrencyInfo(
            "Etherium", "ETH", "2012-07-17", "2020-11-03", 254.8, null
        ))
        return list
    }

    override fun onDestroy() {
        fragmentHomeBinding = null
        super.onDestroy()
    }

    companion object {
        var recyclerType = 1
    }
}