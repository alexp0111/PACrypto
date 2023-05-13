package com.example.pacrypto.data

data class SearchItem(
    var ticker: String = "",
    var name: String? = "",
    var rateCurrent: Double? = null,
    var percents: String? = null,
    var timeUpdate: String? = null,
)