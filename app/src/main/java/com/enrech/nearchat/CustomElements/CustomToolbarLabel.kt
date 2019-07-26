package com.enrech.nearchat.CustomElements

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat

//Esta clase permite guardar todos los métodos y propiedades necesarias para el textview del
//título del toolbar pueda ser animado dinámicamente
class CustomToolbarLabel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    //Variables

    var leftSideBound: Float? = null
    var rightSideBound: Float? = null
    var alphaStored: Float? = null
    var initialAlpha: Float? = null
    var visibilityStored: Int? = null
    var rect: Rect? = null

    //Método de inicio

    //Aqui se inicializan todas las propiedades del elemento del toolbar
    init {
        rect = Rect()
        alphaStored = alpha
        initialAlpha = alpha
        visibilityStored = visibility
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
        visibility = visibilityStored ?: visibility
    }

    //Este método permite obtener la posición exacta del botón en la pantalla
    private fun getBoundaries(){
        val layoutParent = parent as? LinearLayoutCompat
        val toolbarParent = this.parent.parent as? androidx.appcompat.widget.Toolbar

        if (layoutParent != null && toolbarParent != null) {
            getDrawingRect(rect)
            toolbarParent.offsetDescendantRectToMyCoords(this, rect)
            val left = 1 - (rect!!.left.toFloat() / toolbarParent.width)
            val right = 1 - (rect!!.right.toFloat() / toolbarParent.width)

            leftSideBound = left
            rightSideBound = right
        }

    }
}