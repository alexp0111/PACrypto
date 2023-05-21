package com.example.pacrypto.data.worker

import com.example.pacrypto.util.Sub
import java.util.*

/**
 * Item that holds info about separate subscription
 * */
data class SubItem(
    val ticker: String,
    val time: String,
    val weekDay: List<Int> = Sub.weekDays,
    val uuid: UUID
)