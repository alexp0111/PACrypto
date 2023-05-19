package com.example.pacrypto.data.worker

import com.example.pacrypto.util.Sub
import java.util.*

data class SubItem(
    val ticker: String,
    val time: String,
    val weekDay: List<Int> = Sub.weekDays,
    val uuid: UUID
)