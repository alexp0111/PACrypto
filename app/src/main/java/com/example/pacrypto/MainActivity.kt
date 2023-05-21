package com.example.pacrypto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pacrypto.databinding.ActivityMainBinding
import com.example.pacrypto.ui.HomeFragment
import com.example.pacrypto.ui.InfoFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main and the only activity in app
 *
 * if opened from the link - redirect to info page
 * otherwise -> to home screen
 * */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = intent.data

        if (uri != null && uri.pathSegments.size == 2) {
            val params = uri.pathSegments
            val fragment = InfoFragment()
            val bundle = Bundle()
            bundle.putString("ticker", params.first())
            bundle.putString("name", params.last())
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.container_main, fragment).commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_main, HomeFragment()).commit()
        }

    }
}