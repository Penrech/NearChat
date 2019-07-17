package com.enrech.nearchat.CustomElements

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageButton
import android.widget.Toolbar
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.ImageViewCompat
import com.enrech.nearchat.R

class CustomToolbarButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : androidx.appcompat.widget.AppCompatImageButton(context, attrs, defStyleAttr)
{
    var leftSideBound: Float? = null
    var rightSideBound: Float? = null
    var centerSideBound: Float? = null
    var alphaStored: Float? = null
    var initialAlpha: Float? = null
    var elevationStored: Float? = null
    var initialElevation: Float? = null
    var secundaryElevationStored: Float? = null
    var visibilityStored: Int? = null
    var isLeading: Boolean? = null
    var shouldChangeColor: Boolean? = null
    var shouldHideOnChange: Boolean? = null
    var initialBackgroundColor: Int? = null
    var initialElementColor: Int? = null
    var elementBackgroundColor: Int? = null
    var elementIconColor: Int? = null
    var actualDrawable: Drawable? = null
    var initialDrawable: Drawable? = null
    var secundaryDrawable: Drawable? = null
    var rect : Rect? = null

     init {
       attrs?.let {

           val ta = context.obtainStyledAttributes(it, R.styleable.CustomToolbarButton)

           rect = Rect()
           alphaStored = alpha
           initialAlpha = alpha
           elevationStored = elevation
           initialElevation = elevation
           visibilityStored = visibility
           initialBackgroundColor = backgroundTintList?.defaultColor
           elementBackgroundColor = initialBackgroundColor
           initialElementColor = ta.getColor(R.styleable.CustomToolbarButton_icon_initial_tint_color,Color.WHITE)
           Log.i("COLOR","Initial color $initialElementColor")
           elementIconColor = initialElementColor
           setImageDrawable(drawable.mutate())
           actualDrawable = drawable
           initialDrawable = drawable
           val secundaryDrawable = ta.getDrawable(R.styleable.CustomToolbarButton_secundary_drawable)?.mutate()
           secundaryDrawable?.let {
               this.secundaryDrawable = it
           }
           isLeading = ta.getBoolean(R.styleable.CustomToolbarButton_is_leading,false)
           shouldChangeColor = ta.getBoolean(R.styleable.CustomToolbarButton_should_change_color,false)
           shouldHideOnChange = ta.getBoolean(R.styleable.CustomToolbarButton_should_hide_on_change,false)
           secundaryElevationStored = ta.getFloat(R.styleable.CustomToolbarButton_secundary_elevation,0f)

           ta.recycle()
       }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.i("LAYOUT","LAYOUT change")
        getBoundaries()
    }

    fun applyUIChanges(){
        alpha = alphaStored ?: alpha
        elevation = elevationStored ?: elevation
        secundaryDrawable?.let {
            if (drawable != actualDrawable) {
                setImageDrawable(actualDrawable)
            }
        }
        elementBackgroundColor?.let {
            background.setTint(it)
        }
        elementIconColor?.let {
            drawable.setTint(it)
        }
        visibility = visibilityStored ?: visibility


    }

    fun getBoundaries(){
        val layoutParent = parent as? LinearLayoutCompat
        val toolbarParent = layoutParent?.parent as? androidx.appcompat.widget.Toolbar

        if (layoutParent != null && toolbarParent != null) {
            getDrawingRect(rect)
            toolbarParent.offsetDescendantRectToMyCoords(this, rect)
            var left = rect!!.left.toFloat() / toolbarParent.width
            var right = rect!!.right.toFloat() / toolbarParent.width
            var center = rect!!.centerX().toFloat() / toolbarParent.width

            if (!isLeading!!) {
                left = 1 - left
                right= 1 - right
                center = 1 - center
            }

            leftSideBound = right
            rightSideBound = left
            centerSideBound = center

        }
    }


}