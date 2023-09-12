package com.example.pacrypto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.pacrypto.databinding.ActivityMainBinding
import com.example.pacrypto.ui.fragments.InfoFragment
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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val uri = intent.data

        if (uri != null && uri.pathSegments.size == 2) {
            val params = uri.pathSegments
            val fragment = InfoFragment()
            val bundle = Bundle()
            bundle.putString("ticker", params.first())
            bundle.putString("name", params.last())
            fragment.arguments = bundle

            navController.navigate(R.id.action_homeFragment_to_infoFragment, bundle)
        }
    }
}