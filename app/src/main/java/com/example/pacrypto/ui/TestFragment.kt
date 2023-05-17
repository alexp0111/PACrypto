package com.example.pacrypto.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.*
import com.example.pacrypto.R
import com.example.pacrypto.data.worker.SubWorker
import com.example.pacrypto.databinding.FragmentTestBinding
import com.example.pacrypto.viewmodel.CoinViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "TEST_FRAGMENT"

class TestFragment : Fragment(R.layout.fragment_test) {

    private var fragmentTestBinding: FragmentTestBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTestBinding.bind(view)
        fragmentTestBinding = binding

        val downloadRequest = OneTimeWorkRequestBuilder<SubWorker>()
            //.setConstraints(
            //    Constraints.Builder()
            //        .setRequiredNetworkType(
            //            NetworkType.CONNECTED
            //        )
            //        .build()
            //)
            .build()
        val workManager = WorkManager.getInstance(requireContext())

        workManager.getWorkInfosByTag("dld")

        binding.btnUpdate.setOnClickListener {
            workManager
                .beginUniqueWork(
                    "dld",
                    ExistingWorkPolicy.APPEND,
                    downloadRequest
                )
                .enqueue()
        }
    }

    override fun onDestroy() {
        fragmentTestBinding = null
        super.onDestroy()
    }
}
