package com.enrech.nearchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import com.enrech.nearchat.R
import com.enrech.nearchat.fragments.*
import com.enrech.nearchat.interfaces.NotifyInteractionUserProfile
import com.enrech.nearchat.interfaces.NotifyTopFragmentChange
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_root.*

class RootActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, NotifyTopFragmentChange, NotifyInteractionUserProfile {

    private var fragmentHome: HomeFragment? = null
    private var fragmentEvent: EventFragment? = null
    private var fragmentProfile: UserProfileFragment? = null
    private var fragmentMore: MoreFragment? = null

    private var mainFragments: ArrayList<Fragment> = arrayListOf()

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        initTopFragments()
        initNavigationListener()
        loadFragment(fragmentHome,false)
    }

    fun initTopFragments(){
        fragmentHome = HomeFragment()
        fragmentEvent = EventFragment()
        fragmentProfile = UserProfileFragment()
        fragmentMore = MoreFragment()

        mainFragments.add(fragmentHome!!)
        mainFragments.add(fragmentEvent!!)
        mainFragments.add(fragmentProfile!!)
        mainFragments.add(fragmentMore!!)
    }

    fun initNavigationListener(){

        val menuView = RootNavView.getChildAt(0) as BottomNavigationMenuView

        for (i in menuView.iterator()) {
            val itemView = i as BottomNavigationItemView
            val iconView = itemView.getChildAt(0)
            iconView.background = getDrawable(R.drawable.bottom_item_switchable_background)
        }

        RootNavView.setOnNavigationItemSelectedListener(this)
    }

    private fun loadFragment(fragment: Fragment?, shouldAddToBackStack: Boolean) : Boolean {

        if (fragment != null && currentFragment != fragment) {
            val transaction = supportFragmentManager.beginTransaction()

           /* if (!fragment.isAdded){
                transaction.add(R.id.MainRootFragmentContainer,fragment)
            }*/



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

    override fun tabChangeTo(number: Int) {
        val menu = RootNavView.menu
        val fragment = mainFragments[number]
        menu.getItem(number).isChecked = true
        currentFragment = fragment
        Log.i("TIG","cambio a numero $number y fragment $fragment")
    }

    override fun profileOpenEditUserClick(boolean: Boolean) {
        Log.i("TIG","Apreto boton ")
        Log.i("TIG","currentFragment as? UserProfileFragment: ${currentFragment as? UserProfileFragment} ")
        (currentFragment as? UserProfileFragment)?.let {
            Log.i("TIG","Entro en let ")
            it.loadFragment(AddEditUserDetails())
        }
    }

    override fun profilePropagateBackButton() {
       onBackPressed()
    }
}
