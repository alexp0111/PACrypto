package com.example.pacrypto

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pacrypto.data.worker.SubWorker
import com.example.pacrypto.databinding.ActivityMainBinding
import com.example.pacrypto.ui.HomeFragment
import com.example.pacrypto.ui.InfoFragment
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.net.Uri

//TODO:
// 1. Bookmarks
// 2. [updating]
// 3. [ All necessary date for cards in search ]
// 4. --Changing cards-- \ [currency] \ [input type]
// 5. [Info stuff]
// 6. QR
// 7. Notifications

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = intent.data

        if (uri == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container_main, HomeFragment()).commit()
        } else {
            val params = uri.pathSegments
            val fragment = InfoFragment()
            val bundle = Bundle()
            bundle.putString("ticker", params.first())
            bundle.putString("name", params.last())
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.container_main, fragment).commit()
        }
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
    }
}