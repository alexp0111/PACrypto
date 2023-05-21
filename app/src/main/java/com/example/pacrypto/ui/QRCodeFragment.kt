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
import com.example.pacrypto.util.QRInfo
import com.google.zxing.integration.android.IntentIntegrator

/**
 * Simple fragment that launch camera activity
 *  and redirects to info page if result is successful
 * */
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

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val text = IntentIntegrator.parseActivityResult(result.resultCode, data).contents
                    if (text != null && text.matches(Regex(QRInfo().REGEX))) {
                        val fragment = InfoFragment()
                        val bundle = Bundle()
                        bundle.putString(
                            "ticker",
                            text.substringBeforeLast('/').substringAfterLast('/')
                        )
                        bundle.putString("name", text.substringAfterLast('/'))
                        fragment.arguments = bundle

                        parentFragmentManager.beginTransaction().addToBackStack(null)
                            .replace(R.id.container_main, fragment).commit()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            QRInfo(requireContext()).IMPOSSIBLE_TO_READ_ERROR,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        QRInfo(requireContext()).NOT_MATCHES_ERROR,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
}