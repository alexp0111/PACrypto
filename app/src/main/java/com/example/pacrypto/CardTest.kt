package com.example.pacrypto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pacrypto.databinding.FragmentHomeBinding
import com.example.pacrypto.databinding.ItemCoinType1Binding
import com.example.pacrypto.databinding.ItemCoinType2Binding
import com.example.pacrypto.databinding.ItemSubscriptionBinding

class CardTest : Fragment(R.layout.item_subscription) {


    private var binding: ItemSubscriptionBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ItemSubscriptionBinding.bind(view)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

}