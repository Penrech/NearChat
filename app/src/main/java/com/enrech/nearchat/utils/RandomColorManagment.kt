package com.enrech.nearchat.utils

import android.content.Context
import com.enrech.nearchat.R

//Esta clase se utiliza para obtener un color aleatorio con el que se verá el nombre de cada uno de los usuarios en
//un determiado chat
class RandomColorManagment(context: Context) {

    //Propiedades

    //Lista de posibles colores
    private var listOfColors = ArrayList<Int>()
    //Color por si la carga de colores fallara
    private var backupBlue = context.resources.getColor(R.color.defaultBlue,context.theme)

    //Inicialización y carga de los colores a través de los recursos de la aplicación
    init {
        val pattern = "random%d"
        for (i in 0..15) {
            val name = String.format(pattern,i + 1)
            val id = context.resources.getIdentifier(name,"color",context.packageName)
            listOfColors.add(context.resources.getColor(id,context.theme))
        }
    }

    //Esta función es la que devuelve un color aleatorio de los existentes en la lista de colores, si algo falla, devuelve
    //El color por defecto
    fun getRandomColor(): Int {
        val random = (0..15).random()

        if (listOfColors[random] != -1) return listOfColors[random]

        return backupBlue
    }
}