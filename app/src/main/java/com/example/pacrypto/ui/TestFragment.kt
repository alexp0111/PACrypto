package com.example.pacrypto.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pacrypto.R
import com.example.pacrypto.databinding.FragmentInfoBinding
import com.example.pacrypto.databinding.FragmentTestBinding
import com.example.pacrypto.viewmodel.CoinViewModel

class TestFragment: Fragment(R.layout.fragment_test) {

    private var fragmentTestBinding: FragmentTestBinding? = null
    private val viewModel: CoinViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTestBinding.bind(view)
        fragmentTestBinding = binding

        viewModel.assets.observe(viewLifecycleOwner){
            // list of api assets
        }
    }

    override fun onDestroy() {
        fragmentTestBinding = null
        super.onDestroy()
    }
}