package com.enrech.nearchat.utils

import android.animation.ArgbEvaluator
import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.enrech.nearchat.models.DynamicAnimatableToolbarElement
import kotlin.math.abs

//Esta clase es la encargada de gestionar las animaciones dinámicas de la mayoría de los toolbar de la aplicación
class ToolbarAnimationUtils(private val currentActivity: Activity?,
                            private val mainColor: Int,
                            private val secundaryColor: Int,
                            private val displayMetrics: DisplayMetrics) {


    private var toolbarElementsData = ArrayList<DynamicAnimatableToolbarElement>()

    fun setToolbarElementsData(toolbarElementsData: ArrayList<DynamicAnimatableToolbarElement>) {
        this.toolbarElementsData = toolbarElementsData
    }

    //Esta función se ejecuta cada vez que se mueve un determinado viewPager asociado. Recibe el elemento visible
    //Actualmente y el offset del mismo.
    //Con estos dos elementos y una array de elementos del toolbar con sus respectivos parámetros inicializados previamente,
    //en su respectiva actividad, puede animarse de forma genérica y tal y como el diseño de la app requiere, todos los cambios
    //de un toolbar al pasar de un fragment a otro.
    //La implementación se ha diseñado a base de cálculos y prueba y error hasta conseguir un resultado sólido.
    fun changeIconsDinamically(actualPosition: Int, offset: Float) {
        if (actualPosition == 0) {

            toolbarElementsData.forEach {
                if (it.typeOfElement == DynamicAnimatableToolbarElement.TypeOfToolbarElements.TITLE) {
                    val textLeftPosition = (1 - it.leftSideBound)
                    if (offset >= textLeftPosition) {
                        if (it.visibility == View.INVISIBLE) {
                            it.visibility = View.VISIBLE
                        }
                        it.alpha = (abs(textLeftPosition - offset) * 100) / 4
                    } else {
                        it.alpha = 0f
                        if (it.visibility == View.VISIBLE) {
                            it.visibility = View.INVISIBLE
                        }
                    }

                } else {
                    val difference = it.rightSideBound - it.leftSideBound

                    if (offset > it.leftSideBound) {
                        val actualElevation = (maxOf((it.rightSideBound - offset), 0f) / difference)
                        val actualElevationNormalized = actualElevation * 2

                        it.elevation = actualElevationNormalized

                        if (it.shouldChangeColor) {
                            val colorBackground =
                                ArgbEvaluator().evaluate(1 - actualElevation, mainColor, secundaryColor) as Int
                            val colorIcon =
                                ArgbEvaluator().evaluate(actualElevation, mainColor, secundaryColor) as Int

                            it.backgroundColor = colorBackground
                            it.elementColor = colorIcon
                        }

                        if (it.shouldHideOnChange) {
                            it.alpha = actualElevation
                        }

                        it.multipleDrawables?.let {multipleDrawables ->
                            if (offset > it.centerElementBound) {
                                it.elementDrawable = multipleDrawables.second
                            } else if (offset < it.centerElementBound) {
                                it.elementDrawable = multipleDrawables.first
                            }
                        }

                    } else {
                        if (it.shouldChangeColor) {
                            it.backgroundColor = mainColor
                            it.elementColor = secundaryColor
                        }

                        if (it.shouldHideOnChange) {
                            if (offset < it.rightSideBound && offset > it.leftSideBound) {
                                it.alpha = 0f
                            } else {
                                it.alpha = 1f
                            }
                        }

                        it.elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics)

                        it.multipleDrawables?.let { multipleDrawables ->
                            if (it.elementDrawable != multipleDrawables.first){
                                it.elementDrawable = multipleDrawables.first
                            }
                        }

                    }

                }

                //Esta función se llama desde el page change listener de un view pager concreto, y se hace en un thread diferente
                //al de la UI para no entorpecer a esta durante los cálculos de las animaciones. Por ello es necesario plasmar
                //todos los cambios calculados en el hilo de ejecución de la UI especificandolo de forma explicita.
                currentActivity?.runOnUiThread {
                    if (it.typeOfElement == DynamicAnimatableToolbarElement.TypeOfToolbarElements.TITLE) {
                        val title = it.element as TextView
                        title.alpha = it.alpha
                        title.elevation = it.elevation
                        title.visibility = it.visibility
                    } else {
                        val button = it.element as ImageButton
                        button.alpha = it.alpha
                        button.elevation = it.elevation
                        button.visibility = it.visibility
                        it.multipleDrawables?.let { _ ->
                            if (button.drawable != it.elementDrawable) {
                                button.setImageDrawable(it.elementDrawable)
                            }
                        }
                        it.backgroundColor?.let { color -> button.background.setTint(color) }
                        it.elementColor?.let { color -> button.drawable.setTint(color) }

                    }
                }
            }
        }
    }

    //Esta función permite determinar la posición relativa de un determinado botón del toolbar respecto al ancho del dispositivo
    //para poder asi ayudar a que la transición entre una page y otra del view pager sea fluida
    fun getToolbarIconParameters(iconNumberStartingFromRight: Int, toolbarItemHeight: Float): Triple<Float,Float,Float> {
        val margin = 12f
        val marginParsed = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,margin,displayMetrics)
        val size = toolbarItemHeight

        val rightPositionInPX: Float
        val leftPositionInPX: Float
        val centerPositionInPX: Float
        if (iconNumberStartingFromRight == 1) {
            rightPositionInPX = marginParsed
            leftPositionInPX = marginParsed + size
            centerPositionInPX = marginParsed + size / 2
        } else {
            rightPositionInPX = iconNumberStartingFromRight * marginParsed + ( iconNumberStartingFromRight - 1 ) * size
            leftPositionInPX = rightPositionInPX + size
            centerPositionInPX = rightPositionInPX + size / 2
        }

        val widthPX = displayMetrics.widthPixels

        val rightPercentage =  rightPositionInPX  / widthPX
        val leftPercentage = leftPositionInPX  / widthPX
        val centerPercentage = centerPositionInPX / widthPX

        Log.i("DATOS","left desde funcion $leftPercentage")
        return Triple(rightPercentage,leftPercentage,centerPercentage)
    }

    //Esta función permite determinar la posición relativa del textView del título del toolbar respecto al ancho del dispositivo
    //para poder asi ayudar a que la transición entre una page y otra del view pager sea fluida
    fun getToolbarTitleParameters(rightIconNumber: Int, lefIconNumber: Int, toolbatItemHeight: Float): Triple<Float,Float,Float> {
        val margin = 12f
        val marginParsed = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,margin, displayMetrics)
        val size = toolbatItemHeight

        val rightPositionInPX: Float
        val leftPositionInPX: Float
        val centerPositionInPX: Float
        rightPositionInPX = if (rightIconNumber == 1) {
            marginParsed + size
        } else {
            rightIconNumber * marginParsed + marginParsed + rightIconNumber * size
        }
        leftPositionInPX = if (lefIconNumber == 1) {
            marginParsed + size
        } else {
            lefIconNumber * marginParsed + marginParsed + lefIconNumber * size
        }
        centerPositionInPX = (rightPositionInPX - leftPositionInPX) / 2

        val widthPX = displayMetrics.widthPixels

        val rightPercentage =  rightPositionInPX  / widthPX
        val leftPercentage = leftPositionInPX  / widthPX
        val centerPercentage = centerPositionInPX / widthPX

        Log.i("DATOS","left desde funcion $leftPercentage")
        return Triple(leftPercentage,rightPercentage,centerPercentage)

    }
}