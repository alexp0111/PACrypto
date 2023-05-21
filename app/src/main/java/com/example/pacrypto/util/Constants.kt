package com.example.pacrypto.util

import android.content.Context
import com.example.pacrypto.R
import java.util.*


/**
 * Used for animating floating action button
 * */
object AnimationDelays {
    const val FABDelay = 2000L
}

/**
 * Info about shared preferences
 * */
data class Prefs(val context: Context) {
    val FILE_NAME_FAV = "favourite_items"
    val FILE_NAME_SUB = "subscription_items"

    val MESSAGE_DELETED = context.getString(R.string.removed_from_bookmarks)
    val MESSAGE_RESTORE = context.getString(R.string.restore)
}

/**
 * Necessary info for subscriptions
 * */
data class Sub(val context: Context) {
    val CHANNEL_NAME = context.getString(R.string.course_alert)
    val MESSAGE = context.getString(R.string.price_is)

    companion object {
        val weekDays = listOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY,
            Calendar.SUNDAY
        )
        const val CHANNEL_ID = "sub_channel"
    }
}

/**
 * Info for requests & displaying ohlcvs
 * */
data class OhlcvsInfo(val context: Context) {
    val parameters = context.resources.getStringArray(R.array.ohlcvs_info)

    companion object {
        const val REQUEST_PREFIX = "BITSTAMP_SPOT_"
        const val REQUEST_DIVIDER = "_"
        const val PERIOD_ID_1HRS = "1HRS"
        const val PERIOD_ID_2HRS = "2HRS"
        const val PERIOD_ID_8HRS = "8HRS"
        const val PERIOD_ID_1DAY = "1DAY"
        const val PERIOD_ID_5DAY = "5DAY"
        const val PERIOD_ID_1MTH = "1MTH"
        const val PERIOD_LIMIT_1HRS = 24
        const val PERIOD_LIMIT_2HRS = 84
        const val PERIOD_LIMIT_8HRS = 90
        const val PERIOD_LIMIT_1DAY = 90
        const val PERIOD_LIMIT_5DAY = 73
    }
}

object DatePattern {
    const val FULL_INFO = "yyyy-MM-dd'T'HH:mm:ss.SSS"
    const val TIME_ONLY = "HH:mm"
}

data class Errors(val context: Context? = null) {
    // connection
    val CONNECTION_ERROR = "Connection error"
    val IMPOSSIBLE_TO_DOWNLOAD = context?.getString(R.string.failed_to_load_data)
    val IMPOSSIBLE_TO_UPDATE = context?.getString(R.string.data_could_not_be_updated)

    // subs
    val IMPOSSIBLE_TO_SUBSCRIBE = context?.getString(R.string.unable_to_add_subscription)
    val IMPOSSIBLE_TO_UNSUBSCRIBE = context?.getString(R.string.unable_to_unsubscribe)

    // favs
    val IMPOSSIBLE_TO_FAVOURITE = context?.getString(R.string.unable_to_add_to_favorites)
    val IMPOSSIBLE_TO_UNFAVOURITE = context?.getString(R.string.unable_to_remove_from_favorites)

    // Search
    val IMPOSSIBLE_TO_SEARCH = context?.getString(R.string.аailed_to_search)

    // Adapter
    val NO_DATA = context?.getString(R.string.no_data)
}

/**
 * Values of date picker in info fragment
 * */
data class DatePickerValues(val context: Context) {
    val DAY = context.getString(R.string.day)
    val WEEK = context.getString(R.string.week)
    val MONTH = context.getString(R.string.month)
    val QUARTER = context.getString(R.string.quorter)
    val YEAR = context.getString(R.string.year)
    val ALL = context.getString(R.string.all)
}

data class QRInfo(val context: Context? = null) {
    val REGEX = "[A-Za-z]+://[A-Za-z]+\\.[A-Za-z]+/[A-Za-z]+/[A-Za-z]+"
    val IMPOSSIBLE_TO_READ_ERROR = context?.getString(R.string.not_correct_qr)
    val NOT_MATCHES_ERROR = context?.getString(R.string.unrecognizable_qr)
}

object Rates {
    const val USD_MARKER = "$"
    const val RUB_MARKER = "₽"

    const val USD = "USD"
    const val RUB = "RUB"
}

data class Hints(val context: Context) {
    val SEARCH = context.getString(R.string.search)
    val UPDATING = context.getString(R.string.updating)
}

data class Headers(val context: Context) {
    val BOOKMARKS = context.getString(R.string.bookmarks)
    val RESULTS = context.getString(R.string.on_request)
}

/**
 * Info about database names & entity names
 * */
object DatabaseNames {
    const val OHLCVS_NAME = "ohlcvs_database"
    const val SEARCH_NAME = "search_database"

    const val OHLCVS_ENTITY = "ohlcvs"
    const val SEARCH_ENTITY = "search_items"
}