package com.example.pacrypto.util

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity

fun addItemToSP(requireActivity: FragmentActivity, ticker: String) {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME, Context.MODE_PRIVATE)
    val set = HashSet(prefs.getStringSet("favourites", HashSet<String>()))
    set.forEach {
        Log.d("SPPPPP", it)
    }

    set.add(ticker)
    Log.d("SPPPPP", set.size.toString())
    prefs.edit().putStringSet("favourites", set).apply()
}

fun removeItemFromSP(requireActivity: FragmentActivity, ticker: String) {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME, Context.MODE_PRIVATE)
    val set = HashSet(prefs.getStringSet("favourites", HashSet<String>()))
    set.forEach {
        Log.d("SPPPPP", it)
    }

    set.remove(ticker)
    prefs.edit().putStringSet("favourites", set).apply()
}

fun isItemInSP(requireActivity: FragmentActivity, ticker: String): Boolean {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME, Context.MODE_PRIVATE)
    val set = HashSet(prefs.getStringSet("favourites", HashSet<String>()))
    return set.contains(ticker)
}

fun getAllItemsInSP(requireActivity: FragmentActivity): List<String> {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME, Context.MODE_PRIVATE)
    val set = HashSet(prefs.getStringSet("favourites", HashSet<String>()))
    return set.toList()
}