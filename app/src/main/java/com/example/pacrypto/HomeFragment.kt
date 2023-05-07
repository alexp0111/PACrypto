package com.example.pacrypto

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.pacrypto.databinding.FragmentHomeBinding

private const val TAG = "HOME_FRAGMENT"

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var fragmentHomeBinding: FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        fragmentHomeBinding = binding


        binding.ivSub.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container_main, SubscriptionsFragment())
                .addToBackStack(null).commit()
        }
    }

    override fun onDestroy() {
        fragmentHomeBinding = null
        super.onDestroy()
    }
}