package com.enrech.nearchat.activities

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.core.view.iterator
import com.enrech.nearchat.R
import com.enrech.nearchat.navigation.BottomNavigationListener
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // VARIABLES Y FUNCIONES DE NAVEGACIÓN

    //Cada una de las actividades principales posee estas dos variables.
    //La primera es un listener de la custom class BottomNavigationListener que gestiona la navegación con el bottom navigation View
    private var navigationListener: BottomNavigationListener? = null
    //La segunda indica que número de indice tiene la actividad en el menu de su correspondiente bottom navigation View
    private val activityNumber = 0

    //Esta función por un lado inicializa el fondo circular de los iconos del bottom navigation View
    //Por otro lado inicializa la variable navigationListener y el listener onNavigationItemSelectedListener del bottom navigation View
    private fun setBottomNavigation(){
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val menuView: BottomNavigationMenuView = navView.getChildAt(0) as BottomNavigationMenuView

        for (i in menuView.iterator()) {
            val itemView = i as BottomNavigationItemView
            val iconView = itemView.getChildAt(0)
            iconView.background = getDrawable(R.drawable.bottom_item_switchable_background)
        }

        navigationListener = BottomNavigationListener(this)

        navView.setOnNavigationItemSelectedListener(navigationListener)
    }

    //Esta función cambia el icono del bottom navigation view al que le corresponde la actividad actual. Cada actividad
    //Tiene su propio bottom navigation view y esta función los "sincroniza" visualmente.
    private fun updateBottomNavigationSelection(){
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val menu = navView.menu
        menu.getItem(activityNumber).isChecked = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * @see setBottomNavigation
         */
        setBottomNavigation()

        homeTxt.text = "Home activity: $this"
    }

    override fun onResume() {
        super.onResume()

        /**
         * @see updateBottomNavigationSelection
         */
        updateBottomNavigationSelection()
        overridePendingTransition(0,0)
    }


}