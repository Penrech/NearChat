package com.enrech.nearchat.activities

import android.graphics.Point
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.enrech.nearchat.R
import com.enrech.nearchat.fragments.*
import com.enrech.nearchat.interfaces.*
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_root.*
import android.view.Display
import com.enrech.nearchat.models.EventHelper
import android.telephony.TelephonyManager
import com.enrech.nearchat.models.StateFragment

private const val MAX_HISTORIC = 5

class RootActivity : AppCompatActivity(),
    NotifyTopFragmentChange,
    NotifyInteractionUserProfile,
    NotifyInteractionEventTab,
    NotifyInteractionHomeTab,
    ModifyNavigationBarFromFragments {

    companion object {
        const val TAG_ONE = "first"
        const val TAG_SECOND = "second"
        const val TAG_THIRD = "third"
        const val TAG_FOURTH = "fourth"
    }

    //Variables

    private var fragmentHome: HomeFragment? = null
    private var fragmentEvent: EventFragment? = null
    private var fragmentProfile: UserProfileFragment? = null
    private var fragmentMore: MoreFragment? = null

    private var mainFragments: ArrayList<Fragment> = arrayListOf()

    private var currentFragment: Fragment? = null

    private val listState = mutableListOf<StateFragment>()
    private var currentTag: String = TAG_ONE
    private var oldTag: String = TAG_ONE
    private var currentMenuItemId: Int = R.id.navigation_home

    // métodos de vista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        overridePendingTransition(0, 0)

        if (savedInstanceState == null) loadFirstFragment()
        init()

        initTopFragments()
        initNavigationListener()
    }

    // métodos de fragments

    private fun init() {

        RootNavView.setOnNavigationItemSelectedListener { menuItem ->

            if (currentMenuItemId != menuItem.itemId) {

                val fragment: Fragment
                val prevOldTag = oldTag
                oldTag = currentTag
                Log.i("FCYCLE","1. Oldtag es: $oldTag, CurrentTag es: $currentTag")

                currentMenuItemId = menuItem.itemId

                when (currentMenuItemId) {
                    R.id.navigation_home -> {
                        currentTag = TAG_ONE
                        if (currentTag == oldTag) oldTag = prevOldTag
                        Log.i("FCYCLE","1.1 Ahora CurrentTag pasará a ser: $currentTag")
                        fragment = fragmentHome!!
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_event -> {
                        currentTag = TAG_SECOND
                        if (currentTag == oldTag) oldTag = prevOldTag
                        Log.i("FCYCLE","1.1 Ahora CurrentTag pasará a ser: $currentTag")
                        fragment = fragmentEvent!!
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_profile -> {
                        currentTag = TAG_THIRD
                        if (currentTag == oldTag) oldTag = prevOldTag
                        Log.i("FCYCLE","1.1 Ahora CurrentTag pasará a ser: $currentTag")
                        fragment = fragmentProfile!!
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_more -> {
                        currentTag = TAG_FOURTH
                        if (currentTag == oldTag) oldTag = prevOldTag
                        Log.i("FCYCLE","1.1 Ahora CurrentTag pasará a ser: $currentTag")
                        fragment = fragmentMore!!
                        loadFragment(fragment, currentTag)
                    }
                }



                return@setOnNavigationItemSelectedListener true

            }

            false
        }

    }

    private fun loadFirstFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        currentFragment = HomeFragment()
        transaction.add(R.id.MainRootFragmentContainer, currentFragment!!, TAG_ONE)
        transaction.commit()
    }

    private fun loadFragment(fragment: Fragment, tag: String) {

        if (currentFragment !== fragment) {
            val ft = supportFragmentManager.beginTransaction()

            if (fragment.isAdded) {
                ft.hide(currentFragment!!).show(fragment)
            } else {
                ft.hide(currentFragment!!).add(R.id.MainRootFragmentContainer, fragment, tag)
            }

            ft.setPrimaryNavigationFragment(fragment)

            currentFragment = fragment

            ft.commit()

            addBackStack()
        }
    }

    private fun recoverFragment() {

        val lastState = listState.last()
        listState.removeAt(listState.size - 1)

        currentTag = lastState.currentFragmentTag
        oldTag = lastState.oldFragmentTag

        Log.i("FCYCLE","3. Recuperamos la última entra del stack, que es $lastState, con CurrentTag: $currentTag y OldTag: $oldTag")
        Log.i("BACKSTACK","CURRENT TAG: $currentTag , OLD TAG: $oldTag, LIST STATE SIZE : ${listState.size}")

        if (listState.size > 0) {

            val index = listState.size - 1
            val actualState = listState[index]
            if (actualState.currentFragmentTag == actualState.oldFragmentTag) {
                listState.removeAt(index)
            }
            Log.i("FCYLCE","3.1 , el proximo elemento en la lista es $actualState, con CurrentTag: ${actualState.currentFragmentTag} y " +
                    " Old tag: ${actualState.oldFragmentTag}")
            Log.i("BACKSTACK","List state size menor que 0, ACTUAL STATE $actualState, SF current Tag: ${actualState.currentFragmentTag} , " +
                    "SF Old tag: ${actualState.oldFragmentTag}")
        }

        val ft = supportFragmentManager.beginTransaction()

        val currentFragment = supportFragmentManager.findFragmentByTag(currentTag)
        val oldFragment = supportFragmentManager.findFragmentByTag(oldTag)

        if (currentFragment!!.isVisible && oldFragment!!.isHidden) {
            ft.hide(currentFragment).show(oldFragment)
        }

        ft.commit()

        this.currentFragment = oldFragment

        val menu = RootNavView.menu

        when (oldTag) {
            TAG_ONE -> {
                menu.getItem(0).isChecked = true
                currentMenuItemId = menu.getItem(0).itemId
            }
            TAG_SECOND -> {
                menu.getItem(1).isChecked = true
                currentMenuItemId = menu.getItem(1).itemId
            }
            TAG_THIRD -> {
                menu.getItem(2).isChecked = true
                currentMenuItemId = menu.getItem(2).itemId
            }
            TAG_FOURTH -> {
                menu.getItem(3).isChecked = true
                currentMenuItemId = menu.getItem(3).itemId
            }
        }

    }

    private fun addBackStack() {
        if (RootNavView.scrollX != 0) RootNavView.scrollX = 0

        when (listState.size) {
            MAX_HISTORIC -> {

                listState[1].oldFragmentTag = TAG_ONE
                val firstState = listState[1]

                for (i in listState.indices) {
                    if (listState.indices.contains((i + 1))) {
                        listState[i] = listState[i + 1]
                    }
                }

                listState[0] = firstState
                listState[listState.lastIndex] = StateFragment(currentTag, oldTag)
            }
            else -> {
                listState.add(StateFragment(currentTag, oldTag))

                Log.i("FCYCLE","2, Se ha añadido el SF con Currentag: $currentTag y oldTag: $oldTag")

                Log.i("BACKSTACK","ADDED CURRENT TAG: $currentTag, OLD TAG: $oldTag")

                val indexToEliminate = listState.indexOfFirst { it.oldFragmentTag == currentTag }

                Log.i("BACKSTACK","index to elimate: $indexToEliminate")

                if (indexToEliminate != -1 && indexToEliminate != 0){

                    Log.i("FCYCLE","2.1 , anteriormente CurrentTag: $currentTag ya habia sido añadido en el indice $indexToEliminate, por ello se elimina")
                    val sfToEliminate = listState.first { it.oldFragmentTag == currentTag }
                    Log.i("BACKSTACK","SF to eliminate $sfToEliminate")
                    val sfToElimanteCurrentFragment = sfToEliminate.currentFragmentTag

                    listState[indexToEliminate - 1].currentFragmentTag = sfToElimanteCurrentFragment
                    listState.removeAt(indexToEliminate)
                }

            }
        }

    }

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

       // RootNavView.setOnNavigationItemSelectedListener(this)

        Log.i("BOTTOMNAV","anchura de nav: ${RootNavView.width}")
    }

    fun hideBottomNavigation(hide: Boolean){
        when (hide){
            true -> RootNavView.visibility = View.GONE
            false -> RootNavView.visibility = View.VISIBLE
        }
    }

    private fun slideToMatchPlace(currentPosition: Int){
        if (currentPosition == 1) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val width = size.x
            RootNavView.scrollX =  width
        }
    }


    //Métodos delegados del fragment profile

    //Ya que los listener de cada uno de los fragments y subfragments han de estar reflajados en la activity que nos
    //anida, estos métodos responden a diferentes acciones realizadas sobre estos diferentes fragments.

    override fun profileOpenEditUserClick(boolean: Boolean) {
        (currentFragment as? UserProfileFragment)?.loadFragment(AddEditUserDetails())
    }

    override fun profileOpenEditAccessClick() {
        (currentFragment as? UserProfileFragment)?.loadFragment(UserProfileEditAccess())
    }

    override fun profilePropagateBackButton() {
       onBackPressed()
    }

    override fun onBackPressed() {

        if (currentFragment!!.childFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            if (backOnPagerIfNeed()){
                if (listState.size >= 1){
                    recoverFragment()
                } else {
                    super.onBackPressed()
                }
            }
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

    override fun eventOpenAddEditEventClick(isAdd: Boolean) {
        (currentFragment as? EventFragment)?.loadFragment(AddEditEventFragment.newInstance(isAdd))
    }

    override fun eventRecieveLatLngFromMap(latLng: String) {

    }

    override fun eventChangeChatConversationType(typeOfComunication: TypeOfComunication) {
        val currentFragment = (currentFragment as? EventFragment)

        currentFragment?.let {
            val eventCurrentFragment = it.currentFragment
            if (eventCurrentFragment != null && eventCurrentFragment == it.pagerFragment){
               it.pagerFragment?.changeComunicationTypeFromChat(typeOfComunication)
            }
        }
    }

    override fun eventPropagateBackButton() {
        onBackPressed()
    }

    override fun eventOpenGetLocationEventClick(actualLocation: String?) {
        (currentFragment as? EventFragment)?.loadFragment(SetEventLocationFragment())
    }

    override fun showBottomNavigationBar(show: Boolean) {
        hideBottomNavigation(!show)
    }

    override fun slideWithScrollView(offset: Int) {
        Log.i("LOAD", "llamado a slidewithscroll")
        RootNavView.scrollX = offset
    }

    override fun eventPagerLoadedWithCurrentItem(item: Int) {
        slideToMatchPlace(item)
    }

    override fun eventLoadEvent() {
        val eventFragment = (currentFragment as? EventFragment)
        eventFragment?.loadFragment(eventFragment.pagerFragment)
    }

    override fun eventEnd() {
        val eventFragment = (currentFragment as? EventFragment)
        eventFragment?.childFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        eventFragment?.loadFragment(EventHelperFragment.newInstance(EventHelper.TypeOFHelper.EVENTEND))
    }

    override fun eventTooFar() {
        val eventFragment = (currentFragment as? EventFragment)
        eventFragment?.childFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        eventFragment?.loadFragment(EventHelperFragment.newInstance(EventHelper.TypeOFHelper.TOOFAR))

    }

    override fun eventClose() {
        val eventFragment = (currentFragment as? EventFragment)
        eventFragment?.childFragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        eventFragment?.checkOutUserFromEvent()
        eventFragment?.loadFragment(EventHelperFragment.newInstance(EventHelper.TypeOFHelper.NOEVENT))
    }

    override fun homePagerLoadedWithCurrentItem(item: Int) {
        Log.i("BARRA","Delegado recibe pedido de home pager, con item numero $item")
        slideToMatchPlace(item)
    }

    override fun homeMapInitMap() {
        //(currentFragment as? HomePagerFragment)?.moveToUserPosition()
    }

    override fun fragmentLoaded(fragmentTag: String) {
        Log.i("LOAD","Deberia cambiar scroll, actual scroll ${RootNavView.scrollX}")
        runOnUiThread {
            if (RootNavView.scrollX != 0) RootNavView.scrollX = 0
        }
    }

    override fun fragmentUnLoaded(fragmentTag: String) {

    }
}
