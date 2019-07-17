package com.enrech.nearchat.CustomElements

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.widget.LinearLayoutCompat

class CustomToolbarLabel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    var leftSideBound: Float? = null
    var alphaStored: Float? = null
    var initialAlpha: Float? = null
    var visibilityStored: Int? = null
    var rect: Rect? = null

    init {
        rect = Rect()
        alphaStored = alpha
        initialAlpha = alpha
        visibilityStored = visibility
    }

    fun applyUIChanges(){
        alpha = alphaStored ?: alpha
        visibility = visibilityStored ?: visibility
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        getBoundaries()
    }

    fun getBoundaries(){
        val layoutParent = parent as? LinearLayoutCompat
        val toolbarParent = this.parent.parent as? androidx.appcompat.widget.Toolbar

        if (layoutParent != null && toolbarParent != null) {
            getDrawingRect(rect)
            toolbarParent.offsetDescendantRectToMyCoords(this, rect)
            val left = 1 - (rect!!.left.toFloat() / toolbarParent.width)

            leftSideBound = left
        }

    }
}