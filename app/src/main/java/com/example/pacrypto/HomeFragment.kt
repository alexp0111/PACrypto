package com.example.pacrypto

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.pacrypto.databinding.FragmentHomeBinding

private const val TAG = "HOME_FRAGMENT"

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}