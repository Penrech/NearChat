package com.enrech.nearchat.models

import android.graphics.drawable.Drawable

//Esta clase permite almacenar las diferentes propiedades de un elemento de un toolbar necesarias para implementar una
//animación dinámica sobre este cuando se realiza un cambio de pager.

class DynamicAnimatableToolbarElement(
    val element: Any,
    val typeOfElement: TypeOfToolbarElements,
    val leftSideBound: Float,
    val rightSideBound: Float,
    val centerElementBound: Float,
    val shouldChangeColor: Boolean,
    val shouldHideOnChange: Boolean,
    var alpha: Float,
    var elevation: Float,
    var visibility: Int,
    var isLeading: Boolean,
    var backgroundColor: Int? = null,
    var elementColor: Int? = null,
    var elementDrawable: Drawable? = null,
    var multipleDrawables: Pair<Drawable,Drawable>? = null) {

    /**
     * @see element es el elemento en si, es de tipo Any ya que puede ser un textView(title) o un imageButton
     * @see typeOfElement determina si el elemento es un textView o un imageButton
     * @see leftSideBound
     * @see rightSideBound
     * @see centerElementBound
     * determinan las posiciones relativas del elemento en la pantalla, su punto de inicio horizontal, su punto de fin horizontal
     * y su centro horizontal
     * @see shouldChangeColor
     * @see shouldHideOnChange
     * Estas dos propiedades permiten customizar el tipo de animación del elemento, para determinar si este cambia de color
     * al cambiar de fragment o no y para determinar si este se debe ocultar o no con el cambio
     * @see alpha
     * @see elevation
     * @see visibility
     * determinan parametros del elemento que pueden modificarse durante la animación para cambiar la ui del elemento
     *
     * Los últimos cuatro parámetros están referidos exclusivamente a los botones, y determinan que colores y que
     * iconos, drawables, puede tomar el botón durante la animación
     *
     * */

    enum class TypeOfToolbarElements{
        BUTTON, TITLE
    }
}