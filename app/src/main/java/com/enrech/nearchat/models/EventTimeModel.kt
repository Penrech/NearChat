package com.enrech.nearchat.models

import java.text.SimpleDateFormat
import java.util.*

class EventTimeModel constructor(private val locale: Locale, val timeInMillis: Long) {

    private var year: Int? = null
    private var month: Int? = null
    private var day: Int? = null

    private var hour: Int? = null
    private var minute: Int? = null

    private var calendar : Calendar? = null

    init {
        calendar = Calendar.getInstance()
        calendar!!.timeInMillis = timeInMillis
        year = calendar!!.get(Calendar.YEAR)
        month = calendar!!.get(Calendar.MONTH)
        day = calendar!!.get(Calendar.DAY_OF_MONTH)
        hour = calendar!!.get(Calendar.HOUR_OF_DAY)
        minute = calendar!!.get(Calendar.MINUTE)
    }

    fun getTimeInString(): String? {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy HH:mm",locale)

        return dateFormat.format(calendar!!.time)

    }

}