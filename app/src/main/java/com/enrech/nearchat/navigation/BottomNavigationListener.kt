package com.enrech.nearchat.navigation

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.enrech.nearchat.R
import com.enrech.nearchat.activities.EventActivity
import com.enrech.nearchat.activities.MainActivity
import com.enrech.nearchat.activities.MoreActivity
import com.enrech.nearchat.activities.UserProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


//Utilizo esta clase para manejar el cambio de actividades con un bottom navigation View.
//Esta decisión de diseño es porque dentro de cada activity habra 2 o más fragments, si utilizara fragments con el navigation view
//Sería un caos, por ello utilizo actividades independientes.
//He creado esta clase para evitar en lo posible duplicar código.
class BottomNavigationListener(private val context: Context): BottomNavigationView.OnNavigationItemSelectedListener {


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.navigation_home -> {
                //Esta comprobación es para no correr el código si el usuario hace click en el botón de la misma actividad en la que se encuentra
                if (context != MainActivity::class.java) {
                    val intent = Intent(context, MainActivity::class.java)
                    //IMPORTANTE: Este flag evita que ocurra un Stack OverFlow. Si la activity ya esta creada, simplemente
                    //La mueve al frente, sino, crea una nueva pero nunca duplica activities
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    context.startActivity(intent)
                }
                return true
            }
            R.id.navigation_event -> {
                if (context != EventActivity::class.java) {
                    val intent = Intent(context, EventActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    context.startActivity(intent)
                }
                return true
            }
            R.id.navigation_profile -> {
                if (context != UserProfileActivity::class.java) {
                    val intent = Intent(context, UserProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    context.startActivity(intent)
                }
                return true
            }
            R.id.navigation_more -> {
                if (context != MoreActivity::class.java) {
                    val intent = Intent(context, MoreActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    context.startActivity(intent)
                }
                return true
            }
        }
        return false
    }
}