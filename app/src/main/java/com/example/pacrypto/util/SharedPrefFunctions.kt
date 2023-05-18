package com.example.pacrypto.util

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.pacrypto.data.worker.SubItem
import com.google.gson.Gson
import java.util.*
import kotlin.collections.HashSet

// Favourites

fun addFavItemToSP(requireActivity: FragmentActivity, ticker: String) {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME_FAV, Context.MODE_PRIVATE)
    val set = HashSet(prefs.getStringSet("favourites", HashSet<String>()))
    set.forEach {
        Log.d("SPPPPP", it)
    }

    set.add(ticker)
    Log.d("SPPPPP", set.size.toString())
    prefs.edit().putStringSet("favourites", set).apply()
}

fun removeFavItemFromSP(requireActivity: FragmentActivity, ticker: String) {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME_FAV, Context.MODE_PRIVATE)
    val set = HashSet(prefs.getStringSet("favourites", HashSet<String>()))
    set.forEach {
        Log.d("SPPPPP", it)
    }

    set.remove(ticker)
    prefs.edit().putStringSet("favourites", set).apply()
}

fun isFavItemInSP(requireActivity: FragmentActivity, ticker: String): Boolean {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME_FAV, Context.MODE_PRIVATE)
    val set = HashSet(prefs.getStringSet("favourites", HashSet<String>()))
    return set.contains(ticker)
}

fun getAllFavItemsInSP(requireActivity: FragmentActivity): List<String> {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME_FAV, Context.MODE_PRIVATE)
    val set = HashSet(prefs.getStringSet("favourites", HashSet<String>()))
    return set.toList()
}

// Subscriptions

fun isSubItemInSP(requireActivity: FragmentActivity, ticker: String): Boolean {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME_SUB, Context.MODE_PRIVATE)
    val subItem = Gson().fromJson(prefs.getString(ticker, ""), SubItem::class.java)
    return subItem != null
}

fun addSubItemToSP(requireActivity: FragmentActivity, subItem: SubItem) {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME_SUB, Context.MODE_PRIVATE)
    prefs.edit().putString(subItem.ticker, Gson().toJson(subItem)).apply()
}

fun removeSubItemFromSP(requireActivity: FragmentActivity, ticker: String): UUID {
    val prefs =
        requireActivity.getSharedPreferences(Prefs.FILE_NAME_SUB, Context.MODE_PRIVATE)

    val subItem = Gson().fromJson(prefs.getString(ticker, ""), SubItem::class.java)
    prefs.edit().remove(ticker).apply()

    return subItem.uuid
}