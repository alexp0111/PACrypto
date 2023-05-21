package com.example.pacrypto.util

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.example.pacrypto.data.worker.SubItem
import com.google.gson.Gson
import java.util.*

/**
 * This functions is used to work with lightweight info, that stored in shared preferences,
 * such as id's of coins, that are in favourite list & info about subscriptions
 * */

// Favourites

fun addFavItemToSP(requireActivity: FragmentActivity, ticker: String) {
    val prefs =
        requireActivity.getSharedPreferences(
            Prefs(requireActivity.applicationContext).FILE_NAME_FAV,
            Context.MODE_PRIVATE
        )
    val set = prefs.getStringSet("favourites", HashSet())?.let { HashSet(it) }

    set?.add(ticker)
    prefs.edit().putStringSet("favourites", set).apply()
}

fun removeFavItemFromSP(requireActivity: FragmentActivity, ticker: String) {
    val prefs =
        requireActivity.getSharedPreferences(
            Prefs(requireActivity.applicationContext).FILE_NAME_FAV,
            Context.MODE_PRIVATE
        )
    val set = prefs.getStringSet("favourites", HashSet())?.let { HashSet(it) }


    set?.remove(ticker)
    prefs.edit().putStringSet("favourites", set).apply()
}

fun isFavItemInSP(requireActivity: FragmentActivity, ticker: String): Boolean {
    val prefs =
        requireActivity.getSharedPreferences(
            Prefs(requireActivity.applicationContext).FILE_NAME_FAV,
            Context.MODE_PRIVATE
        )
    val set = prefs.getStringSet("favourites", HashSet())?.let { HashSet(it) }
    return set?.contains(ticker) ?: false
}

fun getAllFavItemsInSP(requireActivity: FragmentActivity): List<String> {
    val prefs =
        requireActivity.getSharedPreferences(
            Prefs(requireActivity.applicationContext).FILE_NAME_FAV,
            Context.MODE_PRIVATE
        )
    val set = prefs.getStringSet("favourites", HashSet())?.let { HashSet(it) }
    return set?.toList() ?: listOf()
}

// Subscriptions

fun isSubItemInSP(requireActivity: FragmentActivity, ticker: String): Boolean {
    val prefs =
        requireActivity.getSharedPreferences(
            Prefs(requireActivity.applicationContext).FILE_NAME_SUB,
            Context.MODE_PRIVATE
        )
    val subItem = Gson().fromJson(prefs.getString(ticker, ""), SubItem::class.java)
    return subItem != null
}

fun addSubItemToSP(requireActivity: FragmentActivity, subItem: SubItem) {
    val prefs =
        requireActivity.getSharedPreferences(
            Prefs(requireActivity.applicationContext).FILE_NAME_SUB,
            Context.MODE_PRIVATE
        )
    prefs.edit().putString(subItem.ticker, Gson().toJson(subItem)).apply()
}

fun getAllSubItemsInSP(requireActivity: FragmentActivity): List<SubItem> {
    val list = mutableListOf<SubItem>()
    val prefs =
        requireActivity.getSharedPreferences(
            Prefs(requireActivity.applicationContext).FILE_NAME_SUB,
            Context.MODE_PRIVATE
        )

    prefs.all.forEach {
        list.add(Gson().fromJson(it.value.toString(), SubItem::class.java))
    }

    return list
}

fun removeSubItemFromSP(requireActivity: FragmentActivity, ticker: String): UUID {
    val prefs =
        requireActivity.getSharedPreferences(
            Prefs(requireActivity.applicationContext).FILE_NAME_SUB,
            Context.MODE_PRIVATE
        )

    val subItem = Gson().fromJson(prefs.getString(ticker, ""), SubItem::class.java)
    prefs.edit().remove(ticker).apply()

    return subItem.uuid
}