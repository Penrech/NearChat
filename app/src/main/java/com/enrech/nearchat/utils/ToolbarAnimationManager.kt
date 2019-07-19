package com.enrech.nearchat.utils

import android.animation.ArgbEvaluator
import android.app.Activity
import android.util.Log
import android.view.View
import com.enrech.nearchat.CustomElements.CustomToolbarButton
import com.enrech.nearchat.CustomElements.CustomToolbarLabel
import kotlin.math.abs

//Esta clase es la encargada de gestionar las animaciones dinámicas de la mayoría de los toolbar de la aplicación
class ToolbarAnimationManager(private val currentActivity: Activity?) {

    private var toolbarElementsData = ArrayList<Any>()

    fun setToolbarElementsData(toolbarElementsData: ArrayList<Any>) {
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
            Log.i("CUSTOM","Toolbar elements count ${toolbarElementsData.size}")
            toolbarElementsData.forEach {
                Log.i("CUSTOM","ES CustomButton: ${it is CustomToolbarButton}")
                if (it is CustomToolbarLabel) {
                    val textLeftPosition = it.leftSideBound ?: 0.5f
                    val textRightPosition = it.rightSideBound ?: 0.6f
                    val difference = 0 - textLeftPosition
                    Log.i("OFFSET","left $textLeftPosition")
                    Log.i("OFFSET","diff $difference")
                    Log.i("OFFSET","offsset $offset")

                    if (offset >= textLeftPosition) {
                        if (it.visibilityStored == View.INVISIBLE) {
                            it.visibilityStored = View.VISIBLE
                        }
                        it.alphaStored = 1 - (maxOf((offset), 0f) / difference)
                        Log.i("OFFSET","alpha ${it.alphaStored}")
                    } else {
                        it.alphaStored = 0f
                        if (it.visibilityStored == View.VISIBLE) {
                            it.visibilityStored = View.INVISIBLE
                        }
                    }

                } else if (it is CustomToolbarButton){
                    val left = it.leftSideBound ?: 0.5f
                    val right = it.rightSideBound ?: 0.6f
                    val center = it.centerSideBound ?: 0.55f
                    val difference = right - left

                    if (offset > left) {
                        val actualElevation = (maxOf((right - offset), 0f) / difference)
                        val actualElevationNormalized = actualElevation * 2

                        it.elevationStored = actualElevationNormalized

                        if (it.shouldChangeColor!!) {
                            val colorBackground =
                                ArgbEvaluator().evaluate(1 - actualElevation, it.initialBackgroundColor, it.initialElementColor) as Int
                            val colorIcon =
                                ArgbEvaluator().evaluate(1 - actualElevation, it.initialElementColor, it.initialBackgroundColor) as Int

                            it.elementBackgroundColor = colorBackground
                            it.elementIconColor = colorIcon
                        }

                        if (it.shouldHideOnChange!!) {
                            it.alphaStored = actualElevation
                        }

                        it.secundaryDrawable?.let { drawable ->
                            if (offset > center) {
                                it.actualDrawable = drawable
                            } else {
                                it.actualDrawable = it.initialDrawable
                            }
                        }


                    } else {
                        if (it.shouldChangeColor!!) {
                            it.elementBackgroundColor = it.initialBackgroundColor
                            it.elementIconColor = it.initialElementColor
                        }

                        if (it.shouldHideOnChange!!) {
                            if (offset < right && offset > left) {
                                it.alphaStored = 0f
                            } else {
                                it.alphaStored = 1f
                            }
                        }

                        it.elevationStored = it.initialElevation

                        it.secundaryDrawable?.let { _ ->
                            if (it.actualDrawable != it.initialDrawable) {
                                it.actualDrawable = it.initialDrawable
                            }
                        }

                    }

                }

                //Esta función se llama desde el page change listener de un view pager concreto, y se hace en un thread diferente
                //al de la UI para no entorpecer a esta durante los cálculos de las animaciones. Por ello es necesario plasmar
                //todos los cambios calculados en el hilo de ejecución de la UI especificandolo de forma explicita.
                currentActivity?.runOnUiThread {
                    if (it is CustomToolbarLabel) {
                        it.applyUIChanges()
                    } else if (it is CustomToolbarButton){
                        it.applyUIChanges()
                    }
                }
            }
        }
    }
}