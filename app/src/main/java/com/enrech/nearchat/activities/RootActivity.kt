package com.enrech.nearchat.activities

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.enrech.nearchat.BuildConfig
import com.enrech.nearchat.R
import com.enrech.nearchat.fragments.*
import com.enrech.nearchat.interfaces.*
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import kotlinx.android.synthetic.main.activity_root.*
import com.enrech.nearchat.models.EventHelper
import com.enrech.nearchat.models.StateFragment
import com.enrech.nearchat.services.LocationUpdatesService
import com.enrech.nearchat.utils.Utils
import com.google.android.material.snackbar.Snackbar

private const val MAX_HISTORIC = 5

class RootActivity : AppCompatActivity(),
    NotifyTopFragmentChange,
    NotifyInteractionUserProfile,
    NotifyInteractionEventTab,
    NotifyInteractionHomeTab,
    ModifyNavigationBarFromFragments,
    SharedPreferences.OnSharedPreferenceChangeListener{

    companion object {
        const val TAG_ONE = "first"
        const val TAG_SECOND = "second"
        const val TAG_THIRD = "third"
        const val TAG_FOURTH = "fourth"
    }

    //Variables
    private val TAG = RootActivity::class.java.simpleName

    // Used in checking for runtime permissions.
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private var myReceiver: MyReceiver? = null

    // A reference to the service used to get location updates.
    private var mService: LocationUpdatesService? = null

    // Tracks the bound state of the service.
    private var mBound = false

    // Monitors the state of the connection to the service.
    private val mServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationUpdatesService.LocalBinder
            mService = binder.service
            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
            mBound = false
        }
    }

    private var fragmentHome: HomeFragment? = null
    private var fragmentEvent: EventFragment? = null
    private var fragmentProfile: UserProfileFragment? = null
    private var fragmentMore: MoreFragment? = null

    private var mainFragments: ArrayList<Fragment> = arrayListOf()

    private var currentFragment: Fragment? = null

    private var backButtonPress = false
    private var navigationButtonPress = false

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

            if (currentMenuItemId != menuItem.itemId && !backButtonPress) {

                navigationButtonPress = true

                val fragment: Fragment
                val prevOldTag = oldTag

                oldTag = currentTag

                currentMenuItemId = menuItem.itemId

                when (currentMenuItemId) {
                    R.id.navigation_home -> {
                        currentTag = TAG_ONE
                        if (currentTag == oldTag) oldTag = prevOldTag

                        fragment = fragmentHome!!
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_event -> {
                        currentTag = TAG_SECOND
                        if (currentTag == oldTag) oldTag = prevOldTag

                        fragment = fragmentEvent!!
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_profile -> {
                        currentTag = TAG_THIRD
                        if (currentTag == oldTag) oldTag = prevOldTag

                        fragment = fragmentProfile!!
                        loadFragment(fragment, currentTag)
                    }
                    R.id.navigation_more -> {
                        currentTag = TAG_FOURTH
                        if (currentTag == oldTag) oldTag = prevOldTag

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

        if (listState.size > 0) {

            val index = listState.size - 1
            val actualState = listState[index]
            if (actualState.currentFragmentTag == actualState.oldFragmentTag) {
                listState.removeAt(index)
            }
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

                val indexToEliminate = listState.indexOfFirst { it.oldFragmentTag == currentTag }


                if (indexToEliminate != -1 && indexToEliminate != 0){

                    val sfToEliminate = listState.first { it.oldFragmentTag == currentTag }
                    val sfToElimanteCurrentFragment = sfToEliminate.currentFragmentTag

                    listState[indexToEliminate - 1].currentFragmentTag = sfToElimanteCurrentFragment
                    listState.removeAt(indexToEliminate)
                }

            }
        }

        navigationButtonPress = false

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

    //Ya que los listener de cada uno de los fragments y subfragments han de estar reflajados en la activity que los
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

        if (navigationButtonPress) return

        backButtonPress = true

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

        backButtonPress = false
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

    override fun homeMapInitMap() {}

    override fun fragmentLoaded(fragmentTag: String) {
        runOnUiThread {
            if (RootNavView.scrollX != 0) RootNavView.scrollX = 0
        }
    }

    override fun fragmentUnLoaded(fragmentTag: String) {}

    // Gestión servicio de localización

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
            if (location != null) {
                Toast.makeText(
                    this@RootActivity, Utils.getLocationText(location),
                    Toast.LENGTH_SHORT
                ).show()
                Log.i("TAG", "Recibo datos aun en background")
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            Snackbar.make(
                rootActivityRoot,
                R.string.permission_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.ok, View.OnClickListener {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@RootActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                })
                .show()
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this@RootActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> // Permission was granted.
                    mService?.requestLocationUpdates()
                else -> // Permission denied.
                    //setButtonsState(false)
                    Snackbar.make(
                        rootActivityRoot,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(R.string.settings, View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID, null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        })
                        .show()
            }
        }
    }
}
