package com.example.pacrypto.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.pacrypto.R
import com.example.pacrypto.databinding.FragmentQRCodeBinding
import com.google.zxing.integration.android.IntentIntegrator

class QRCodeFragment : Fragment(R.layout.fragment_q_r_code) {

    private var fragmentQRCodeBinding: FragmentQRCodeBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentQRCodeBinding.bind(view)
        fragmentQRCodeBinding = binding

        binding.fabScan.setOnClickListener {
            val integrator = IntentIntegrator(requireActivity())
            integrator.setPrompt("")
            integrator.setOrientationLocked(true)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            resultLauncher.launch(integrator.createScanIntent())
        }
    }

    override fun onDestroy() {
        fragmentQRCodeBinding = null
        super.onDestroy()
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    val result = IntentIntegrator.parseActivityResult(result.resultCode, data)
                    val text = result.contents
                    if (text != null && text.matches(Regex("[A-Za-z0-9]+-[A-Za-z0-9]+"))) {
                        val fragment = InfoFragment()
                        val bundle = Bundle()
                        bundle.putString("ticker", text.substringBefore('-'))
                        bundle.putString("name", text.substringAfter('-'))
                        fragment.arguments = bundle

                        parentFragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.container_main, fragment).commit()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Кажется, данный QR-код не корректен",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Мы не можем распознать этот код",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
}