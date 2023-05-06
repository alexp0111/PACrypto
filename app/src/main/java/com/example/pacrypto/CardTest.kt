package com.example.pacrypto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pacrypto.databinding.FragmentHomeBinding
import com.example.pacrypto.databinding.ItemCoinType1Binding

class CardTest : Fragment(R.layout.item_coin_type_1) {


    private var binding: ItemCoinType1Binding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ItemCoinType1Binding.bind(view)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

}