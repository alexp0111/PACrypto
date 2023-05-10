package com.example.pacrypto.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.example.pacrypto.R
import com.example.pacrypto.databinding.FragmentSubscriptionsBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SUBSCRIPTIONS_FRAGMENT"

@AndroidEntryPoint
class SubscriptionsFragment : Fragment(R.layout.fragment_subscriptions) {

    private var fragmentSubscriptionsBinding: FragmentSubscriptionsBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSubscriptionsBinding.bind(view)
        fragmentSubscriptionsBinding = binding

        binding.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        fragmentSubscriptionsBinding = null
        super.onDestroy()
    }
}