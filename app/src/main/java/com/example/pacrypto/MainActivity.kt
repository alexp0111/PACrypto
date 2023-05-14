package com.example.pacrypto

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pacrypto.databinding.ActivityMainBinding
import com.example.pacrypto.ui.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

//TODO:
// 1. Bookmarks
// 2. updating
// 3. All necessary date for cards in search
// 4. Changing cards \ [currency] \ [input type]
// 5. Info stuff
// 6. QR
// 7. Notifications

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