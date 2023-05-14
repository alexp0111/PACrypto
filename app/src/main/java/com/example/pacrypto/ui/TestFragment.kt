package com.example.pacrypto.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.pacrypto.R
import com.example.pacrypto.databinding.FragmentTestBinding
import com.example.pacrypto.util.UiState
import com.example.pacrypto.viewmodel.CoinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "TEST_FRAGMENT"

@AndroidEntryPoint
class TestFragment : Fragment(R.layout.fragment_test) {

    private var fragmentTestBinding: FragmentTestBinding? = null
    private val viewModel: CoinViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTestBinding.bind(view)
        fragmentTestBinding = binding

        binding.btnUpdate.setOnClickListener {
            viewModel.updateAssets()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allUSDRatesAct.collect {
                    val result = it ?: return@collect


                    if (result is UiState.Success) {
                        Log.d(TAG, result.data!!.rates.size.toString())
                        val list = result.data.rates
                        var str = ""
                        list.forEach {
                            str += it.rate.toString() + "/" + it.asset_id_quote + "/" + it.time + "\n"
                        }
                        binding.tvTest.text = str
                    } else if (result is UiState.Failure) {
                        binding.tvTest.text = result.error
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allAssets.collect {
                    val result = it ?: return@collect


                    if (result is UiState.Success) {
                        Log.d(TAG, result.data!!.size.toString())
                        val list = result.data
                        var str = ""
                        list!!.forEach {
                            str += it.asset_id + "/" + it.name + "\n"
                        }
                        // binding.tvTest.text = str
                    } else if (result is UiState.Failure) {
                        binding.tvTest.text = result.error
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onDestroy() {
        fragmentTestBinding = null
        super.onDestroy()
    }
}
