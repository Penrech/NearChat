package com.enrech.nearchat.CustomElements

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import java.util.*


class CustomTimePickerDialog @JvmOverloads constructor(context: Context,
                                                       onTimeSetListener : OnTimeSetListener,
                                                       hourOfDay: Int,
                                                       minute: Int,
                                                       is24HourView: Boolean,
                                                       private val isActualDay: Boolean) : TimePickerDialog(context,onTimeSetListener,hourOfDay,minute,is24HourView){

    private var lastHour: Int? = null
    private var lastMinute: Int? = null

    init {
        checkIfForbibbenTime(hourOfDay,minute)
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        super.onTimeChanged(view, hourOfDay, minute)

        checkIfForbibbenTime(hourOfDay,minute)

    }

    private fun checkIfForbibbenTime(hourOfDay: Int,minute: Int){
        val rightNow = Calendar.getInstance()

        if (isActualDay) {

            val currentHour = rightNow.get(Calendar.HOUR_OF_DAY)
            val currentMinute = rightNow.get(Calendar.MINUTE)

            val validTime: Boolean
            if (hourOfDay < currentHour) {
                validTime = false
            } else if (hourOfDay == currentHour) {
                validTime = minute >= currentMinute
            } else {
                validTime = true
            }


            if (!validTime) {
                updateTime(currentHour, currentMinute)
                lastHour = currentHour
                lastMinute = currentMinute
            } else {
                lastHour = hourOfDay
                lastMinute = hourOfDay
            }

        }
    }
}