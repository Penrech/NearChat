package com.enrech.nearchat.CustomElements

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.LinearLayoutCompat
import com.enrech.nearchat.R

//Esta clase permite guardar todos los métodos y propiedades necesarias para que los diferentes
//Botones del toolbar puedan ser animados dinámicamente
class CustomToolbarButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : androidx.appcompat.widget.AppCompatImageButton(context, attrs, defStyleAttr)
{

    //Variables

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

    //Método de inicio

    //Aqui se inicializan todas las propiedades del elemento del toolbar, parte de ellas provienen de los stylables custom
    //Definidos para este elemento en res/values/attrs.xml
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

    //métodos sobrescritos

    //Ya que la vista del botón y la de sus elementos superiores en la herarquia no está cargada todavía en init, se llama
    //a la función encargada de determinar la posición del elemento en esta función en la que si está la herarquia cargada
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        getBoundaries()
    }

    //métodos

    //Esta función permite cambiar de una vez todos los parámetros visuales del botón
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

    //Este método permite obtener la posición exacta del botón en la pantalla
    private fun getBoundaries(){
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