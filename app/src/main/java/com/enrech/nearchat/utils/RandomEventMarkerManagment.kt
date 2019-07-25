package com.enrech.nearchat.utils

import android.content.Context
import android.graphics.drawable.Drawable
import com.enrech.nearchat.R

class RandomEventMarkerManagment() {

    fun getRandomEventMarker(context: Context): Drawable {
        val random = (0..15).random()

        val pattern = "event_marker_%d"
        val name = String.format(pattern,random + 1)
        val id = context.resources.getIdentifier(name,"drawable",context.packageName)
        return context.resources.getDrawable(id,context.theme)
    }
}