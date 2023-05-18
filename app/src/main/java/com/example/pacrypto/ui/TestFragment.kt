package com.example.pacrypto.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pacrypto.R
import com.example.pacrypto.data.worker.SubWorker
import com.example.pacrypto.databinding.FragmentTestBinding
import java.time.Duration
import java.util.*

private const val TAG = "TEST_FRAGMENT"

class TestFragment : Fragment(R.layout.fragment_test) {

    private var fragmentTestBinding: FragmentTestBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTestBinding.bind(view)
        fragmentTestBinding = binding

        //val downloadRequest = OneTimeWorkRequestBuilder<SubWorker>()
        //    //.setConstraints(
        //    //    Constraints.Builder()
        //    //        .setRequiredNetworkType(
        //    //            NetworkType.CONNECTED
        //    //        )
        //    //        .build()
        //    //)
        //    .build()
        val downloadRequest = PeriodicWorkRequestBuilder<SubWorker>(Duration.ofMillis(5000L))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            ).build()
        val x = downloadRequest.id
        val workManager = WorkManager.getInstance(requireContext())
        workManager.cancelAllWork()

        binding.btnUpdate.setOnClickListener {
            workManager.enqueue(downloadRequest)
        }

        binding.btnRemove.setOnClickListener {
            workManager.cancelWorkById(x)
        }
    }

    override fun onDestroy() {
        fragmentTestBinding = null
        super.onDestroy()
    }
}
