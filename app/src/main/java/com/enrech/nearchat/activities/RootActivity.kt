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

private const val TAG_ONE = "first"
private const val TAG_SECOND = "second"
private const val TAG_THIRD = "third"
private const val TAG_FOURTH = "fourth"
private const val MAX_HISTORIC = 5

class RootActivity : AppCompatActivity(),
    NotifyTopFragmentChange,
    NotifyInteractionUserProfile,
    NotifyInteractionEventTab,
    NotifyInteractionHomeTab,
    ModifyNavigationBarFromFragments {

    //Variables

    private var fragmentHome: HomeFragment? = null
    private var fragmentEvent: EventFragment? = null
    private var fragmentProfile: UserProfileFragment? = null
    private var fragmentMore: MoreFragment? = null

   /* private val fragmentHomeTag = "home"
    private val fragmentEventTag = "event"
    private val fragmentProfileTag = "profile"
    private val fragmentMoreTag = "more"*/

    private var mainFragments: ArrayList<Fragment> = arrayListOf()

    private var currentFragment: Fragment? = null

    private val listState = mutableListOf<StateFragment>()
    private var currentTag: String = TAG_ONE
    private var oldTag: String = TAG_ONE
    private var currentMenuItemId: Int = R.id.navigation_home

   /* private var backStackListening = false

    private var currentBackStackNumber = 0

    private var backStackChangeListener = object : FragmentManager.OnBackStackChangedListener {

        override fun onBackStackChanged() {
            Log.i("BACKSTACK","currentFragment before: $currentFragment")
            val newCount = supportFragmentManager.backStackEntryCount
            currentFragment = supportFragmentManager.fragments.last()
            if (newCount < currentBackStackNumber) {
                tabChangedOnBackStackPop()
            }
            Log.i("BACKSTACK","New count: $newCount")
            Log.i("BACKSTACK","Old count: $currentBackStackNumber")
            Log.i("BACKSTACK","currentFragment after: $currentFragment")

            currentBackStackNumber = newCount
        }

    }*/

    // métodos de vista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        overridePendingTransition(0, 0)

        if (savedInstanceState == null) loadFirstFragment()
        init()

        initTopFragments()
        initNavigationListener()
        //loadFragment(fragmentHome,fragmentHomeTag,false)
    }

    override fun onPause() {
        super.onPause()

        //removeBackStackChangeListener()
    }

    override fun onResume() {
        super.onResume()

       // addBackStackChangeListener()
    }

    // métodos de fragments

    private fun init() {

        RootNavView.setOnNavigationItemSelectedListener { menuItem ->

            Log.i("FRAGMENT","Current Menu item: $currentMenuItemId, menu item: ${menuItem.itemId}")

            if (currentMenuItemId != menuItem.itemId) {

                val fragment: Fragment
                oldTag = currentTag

                currentMenuItemId = menuItem.itemId

                when (currentMenuItemId) {
                    R.id.navigation_home -> {
                        currentTag = TAG_ONE
                        fragment = fragmentHome!!
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_event -> {
                        currentTag = TAG_SECOND
                        fragment = fragmentEvent!!
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_profile -> {
                        currentTag = TAG_THIRD
                        fragment = fragmentProfile!!
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_more -> {
                        currentTag = TAG_FOURTH
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

        Log.i("FRAGMENT","Current fragment: $currentFragment, fragment: $fragment")

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

        val ft = supportFragmentManager.beginTransaction()

        val currentFragment = supportFragmentManager.findFragmentByTag(currentTag)
        val oldFragment = supportFragmentManager.findFragmentByTag(oldTag)

        if (currentFragment!!.isVisible && oldFragment!!.isHidden) {
            ft.hide(currentFragment).show(oldFragment)
        }

        ft.commit()

        Log.d("FRAGMENT", "$currentTag - $oldTag")
        Log.d("FRAGMENT", "Current Fragment $currentFragment - old fragment $oldFragment")

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

    //Like YouTube
    private fun addBackStack() {
        if (RootNavView.scrollX != 0) RootNavView.scrollX = 0

        Log.d("thr add", "$currentTag - $oldTag")

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

                val indexToEliminate = listState.indexOfFirst { it.oldFragmentTag == currentTag }
                if (indexToEliminate != -1 && indexToEliminate != 0){
                    val sfToEliminate = listState.first { it.oldFragmentTag == currentTag }
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

    /*
    *
    * val prev = childFragmentManager.findFragmentByTag(fragmentTag)
                if (prev != null) {
                    transaction.remove(prev)
                }
                transaction.addToBackStack(fragmentTag)
    * */

/*
    //Esta función es la cargada de introducir los fragments en el contenedor de la actividad destinado a ello
    private fun loadFragment(fragment: Fragment?, fragmentTag: String, shouldAddToBackStack: Boolean) : Boolean {


        if (fragment != null && currentFragment != fragment) {

            val entryToDelete = deleteFragmentFromBackStackIfIsInIt(fragmentTag)



            /*entryToDelete?.let {
                supportFragmentManager.popBackStack(it.id,FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }*/

            val prevFragment = supportFragmentManager.findFragmentByTag(fragmentTag)
            if (prevFragment != null) {
                /*val transaction2 = supportFragmentManager.beginTransaction()
                transaction2.remove(prevFragment).commitNow()*/
                val currentPosition = supportFragmentManager.fragments.indexOf(prevFragment)
            }
            val transaction = supportFragmentManager.beginTransaction()

            transaction.setReorderingAllowed()
            transaction.add(R.id.MainRootFragmentContainer,fragment,fragmentTag)

            if (shouldAddToBackStack && prevFragment == null) {
                transaction.addToBackStack(fragmentTag)
            }

            transaction.setPrimaryNavigationFragment(fragment)
            transaction.commit()

            if (prevFragment != null){
                currentFragment = fragment
            }
            //

            return true
        }

        return false
    }*/

    /*private fun deleteFragmentFromBackStackIfIsInIt(newTag: String) : FragmentManager.BackStackEntry?{
        var entryToDelete: FragmentManager.BackStackEntry? = null

        for (index in 0 until supportFragmentManager.backStackEntryCount) {

            if (supportFragmentManager.getBackStackEntryAt(index).name.equals(newTag,true)) {
                entryToDelete = supportFragmentManager.getBackStackEntryAt(index)
            }
        }

        return entryToDelete
    }*/

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

    //Métodos de los delegados

    // Métodos delegados de navegación

    //Este método es llamado cuando se selecciona algun menu item del navigation view
    /*override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        var fragment: Fragment? = null
        val fragmentTag: String

        when (p0.itemId) {
            R.id.navigation_home -> {
                fragment = fragmentHome
                fragmentTag = fragmentHomeTag
            }
            R.id.navigation_event -> {
                fragment = fragmentEvent
                fragmentTag = fragmentEventTag
            }
            R.id.navigation_profile -> {
                fragment = fragmentProfile
                fragmentTag = fragmentProfileTag
            }
            else -> {
                fragment = fragmentMore
                fragmentTag = fragmentMoreTag
            }
        }

        return loadFragment(fragment,fragmentTag,true)
    }*/


    private fun slideToMatchPlace(currentPosition: Int){
        if (currentPosition == 1) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            val width = size.x
            RootNavView.scrollX =  width
        }
    }

    //Estos dos métodos añaden o eliminan el listener encargado de gestionar que fragmento se está visualizando al
    //Usar el boton de navegación hacia atras
   /* private fun addBackStackChangeListener(){
        if (!backStackListening) {
            supportFragmentManager.addOnBackStackChangedListener(backStackChangeListener)
            backStackListening = true
        }
    }

    private fun removeBackStackChangeListener(){
        if (backStackListening) {
            supportFragmentManager.removeOnBackStackChangedListener(backStackChangeListener)
            backStackListening = false
        }
    }*/

    //Este método es llamado cada vez que un nuevo fragment principal es cargado para indicar de forma consistente
    //cual es el fragment visible actualmente
    override fun tabChangeTo(number: Int) {
        /*val menu = RootNavView.menu
        val fragment = mainFragments[number]
        menu.getItem(number).isChecked = true
        currentFragment = fragment
        if (RootNavView.scrollX != 0) RootNavView.scrollX = 0*/

    }

    fun tabChangedOnBackStackPop(){
        var number = 0
        (currentFragment as? HomeFragment)?.let {
            number = 0
        }
        (currentFragment as? EventFragment)?.let {
            number = 1
        }
        (currentFragment as? UserProfileFragment)?.let {
            number = 2
        }
        (currentFragment as? MoreFragment)?.let {
            number = 3
        }

        val menu = RootNavView.menu
        menu.getItem(number).isChecked = true
        if (RootNavView.scrollX != 0) RootNavView.scrollX = 0
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
                if (listState.size > 1){
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
        slideToMatchPlace(item)
    }

    override fun homeMapInitMap() {
        //(currentFragment as? HomePagerFragment)?.moveToUserPosition()
    }
}
