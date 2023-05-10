package com.example.pacrypto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pacrypto.databinding.ActivityMainBinding
import com.example.pacrypto.ui.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container_main, HomeFragment()).commit()
    }
}