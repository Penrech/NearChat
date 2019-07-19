package com.enrech.nearchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.enrech.nearchat.R
import com.enrech.nearchat.fragments.*
import com.enrech.nearchat.interfaces.NotifyInteractionUserProfile
import com.enrech.nearchat.interfaces.NotifyTopFragmentChange
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_root.*

class RootActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, NotifyTopFragmentChange, NotifyInteractionUserProfile {

    //Variables

    private var fragmentHome: HomeFragment? = null
    private var fragmentEvent: EventFragment? = null
    private var fragmentProfile: UserProfileFragment? = null
    private var fragmentMore: MoreFragment? = null

    private var mainFragments: ArrayList<Fragment> = arrayListOf()

    private var currentFragment: Fragment? = null

    // métodos de vista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        initTopFragments()
        initNavigationListener()
        loadFragment(fragmentHome,false)
    }

    // métodos de fragments

    //Este método inicializa los cuatro fragments principales de la aplicación, y se guarda siempre una instacia de ellos
    //para mantener de forma sencilla su instancia aun cambiando de tab y volviendo nuevamente a ella
    private fun initTopFragments(){
        fragmentHome = HomeFragment()
        fragmentEvent = EventFragment()
        fragmentProfile = UserProfileFragment()
        fragmentMore = MoreFragment()

        mainFragments.add(fragmentHome!!)
        mainFragments.add(fragmentEvent!!)
        mainFragments.add(fragmentProfile!!)
        mainFragments.add(fragmentMore!!)
    }

    //Esta función es la cargada de introducir los fragments en el contenedor de la actividad destinado a ello
    private fun loadFragment(fragment: Fragment?, shouldAddToBackStack: Boolean) : Boolean {

        if (fragment != null && currentFragment != fragment) {
            val transaction = supportFragmentManager.beginTransaction()

            if (shouldAddToBackStack) {
                transaction.addToBackStack(null)
            }

            transaction.replace(R.id.MainRootFragmentContainer, fragment)
            transaction.setPrimaryNavigationFragment(fragment)
            transaction.commit()

            currentFragment = fragment

            return true
        }

        return false
    }

    //Métodos de navegación

    //Esta función por un lado inicializa el fondo circular de los iconos del bottom navigation View
    //Por otro lado inicializa la variable navigationListener y el listener onNavigationItemSelectedListener del bottom navigation View
    private fun initNavigationListener(){

        val menuView = RootNavView.getChildAt(0) as BottomNavigationMenuView

        for (i in menuView.iterator()) {
            val itemView = i as BottomNavigationItemView
            val iconView = itemView.getChildAt(0)
            iconView.background = getDrawable(R.drawable.bottom_item_switchable_background)
        }

        RootNavView.setOnNavigationItemSelectedListener(this)
    }

    //Métodos de los delegados

    // Métodos delegados de navegación

    //Este método es llamado cuando se selecciona algun menu item del navigation view
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        var fragment: Fragment? = null

        when (p0.itemId) {
            R.id.navigation_home -> {
                fragment = fragmentHome
            }
            R.id.navigation_event -> {
                fragment = fragmentEvent
            }
            R.id.navigation_profile -> {
                fragment = fragmentProfile
            }
            R.id.navigation_more -> {
                fragment = fragmentMore
            }
        }

        return loadFragment(fragment,true)
    }


    //Este método es llamado cada vez que un nuevo fragment principal es cargado para indicar de forma consistente
    //cual es el fragment visible actualmente
    override fun tabChangeTo(number: Int) {
        val menu = RootNavView.menu
        val fragment = mainFragments[number]
        menu.getItem(number).isChecked = true
        currentFragment = fragment

    }

    //Métodos delegados del fragment profile

    //Ya que los listener de cada uno de los fragments y subfragments han de estar reflajados en la activity que nos
    //anida, estos métodos responden a diferentes acciones realizadas sobre estos diferentes fragments.

    override fun profileOpenEditUserClick(boolean: Boolean) {
        (currentFragment as? UserProfileFragment)?.loadFragment(AddEditEventFragment())
    }

    override fun profilePropagateBackButton() {
       onBackPressed()
    }

    override fun onBackPressed() {
        if (backOnPagerIfNeed()){
            super.onBackPressed()
        }

    }

    //Este método permite sincronizar la accion de backbutton con la posicion de las páginas en los view pager anidados
    private fun backOnPagerIfNeed(): Boolean{
        if (currentFragment != null) {

            when (currentFragment) {
                is HomeFragment -> {
                    val fragment = (currentFragment as HomeFragment)
                    if (fragment.currentFragment == fragment.pagerFragment){
                        if (fragment.pagerFragment?.getActualPage() != 0)  {
                            fragment.pagerFragment?.setPagerPageBackwards()
                            return false
                        }
                    }
                }
                is EventFragment -> {
                    val fragment = (currentFragment as EventFragment)
                    if (fragment.currentFragment == fragment.pagerFragment){
                        if (fragment.pagerFragment?.getActualPage() != 0)  {
                            fragment.pagerFragment?.setPagerPageBackwards()
                            return false
                        }
                    }
                }
                is UserProfileFragment -> {
                    val fragment = (currentFragment as UserProfileFragment)
                    return true
                }
            }
        }

        return true
    }
}
