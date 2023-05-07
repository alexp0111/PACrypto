package com.example.pacrypto

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pacrypto.databinding.FragmentHomeBinding
import com.example.pacrypto.databinding.FragmentSubscriptionsBinding

private const val TAG = "SUBSCRIPTIONS_FRAGMENT"


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