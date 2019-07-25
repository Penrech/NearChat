package com.enrech.nearchat.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.viewpager.widget.ViewPager

import com.enrech.nearchat.R
import com.enrech.nearchat.adapters.DefaultPagerAdapter
import com.enrech.nearchat.interfaces.ModifyNavigationBarFromFragments
import com.enrech.nearchat.interfaces.NotifyInteractionHomeTab
import com.enrech.nearchat.utils.CountriesObject
import com.enrech.nearchat.utils.LocationWithoutGPSUtils
import com.enrech.nearchat.utils.ToolbarAnimationManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_home_pager.*
import kotlinx.android.synthetic.main.fragment_home_pager.view.*
import kotlinx.android.synthetic.main.fragment_home_pager.view.LinerLayoutHomeToolbar
import kotlin.math.max

private const val LOCATION_REQUEST_CODE = 101

//Este fragment se encarga de las pager que conforman el mapa y la lista eventos cercanos
class HomePagerFragment : Fragment() , ViewPager.OnPageChangeListener{

    //Variables

    private var mPager: ViewPager? = null
    private var pagerAdapter: DefaultPagerAdapter? = null
    private var pagerFragments: ArrayList<Fragment> = arrayListOf()

    private var homeMapFragment: HomeMapFragment? = null
    private var homeListFragment: EventListFragment? = null

    private var toolbarElementsData = ArrayList<Any>()

    private var toolbarAnimationUtils: ToolbarAnimationManager? = null

    private var bottomNavigationListener: ModifyNavigationBarFromFragments? = null

    private var notifyInteractionHomeTab: NotifyInteractionHomeTab? = null

    private var pagerIsListening = false

    var currentPage : Int? = null

    var isScrolling: Boolean = false

    private var locationManager: LocationManager? = null

    private var lastLocation: Location? = null

    private var locationListenerActive = false

    //Listener

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        isScrolling = positionOffset != 0.0f

        run{
            toolbarAnimationUtils?.changeIconsDinamically(position,positionOffset)
        }
        if (position == 0) {
            bottomNavigationListener?.slideWithScrollView(positionOffsetPixels)
        }
    }

    override fun onPageSelected(position: Int) {
        currentPage = position
    }

    private var changePageButtonListener = View.OnClickListener {
        if (mPager?.currentItem == 0) {
            mPager?.currentItem = 1
        } else {
            mPager?.currentItem = 0
        }
    }

    private var myPositionButtonListener = View.OnClickListener {
        if (!isScrolling) {
            if (mPager?.currentItem == 0) {
                requestPermission()
            }
        }
    }

    private var locationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location?) {
            if (lastLocation == null) {
                moveToUserPosition()
                if (p0 != null) homeMapFragment?.setInitialLocationFromGps(LatLng(p0.latitude,p0.longitude))
            }
            lastLocation = p0
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

            moveToUserPosition()
        }

        override fun onProviderDisabled(p0: String?) {
            homeMapFragment?.removeMyPositionUserMarker()
        }
    }

    //Métodos lifeCycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPagerFragments()
        setInitLocationFromGpsToMapIfPosible()
        initLocation()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPager = view.homeFragmentsPager

        setUpToolbar()

        setUpPager()

        setUpButtons()

        notifyInteractionHomeTab?.homePagerLoadedWithCurrentItem(mPager!!.currentItem)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ModifyNavigationBarFromFragments){
            bottomNavigationListener = context
        }
        if (context is NotifyInteractionHomeTab) {
            notifyInteractionHomeTab = context
        }
        else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavigationListener = null
        notifyInteractionHomeTab = null
    }

    override fun onResume() {
        super.onResume()

        listenPagerEvents()

        if (homeMapFragment?.isGmapInit() != null && !locationListenerActive) {
            requestPermission()
        }
    }

    override fun onPause() {
        super.onPause()

        if (locationListenerActive) {
            locationManager?.removeUpdates(locationListener)
            locationListenerActive = false
        }

        deleteListenerPagerEvents()
    }

    //Métodos

    //Métodos pager

    //Esta función inicializa el pager
    private fun setUpPager(){

        pagerAdapter = DefaultPagerAdapter(childFragmentManager,pagerFragments)

        mPager?.adapter = pagerAdapter

        currentPage?.let {
            mPager?.currentItem = it
        }

        setToolbarUIAfterPageChange()
    }

    //Esta función inicia, si es necesario, el listener
    //encargado de escuchar los cambios entre las páginas del view pager
    private fun listenPagerEvents(){
        if (!pagerIsListening) {
            mPager?.addOnPageChangeListener(this)
            pagerIsListening = true
        }
    }

    //Esta función elimina, si es necesario, el listener
    //encargado de escuchar los cambios entre las páginas del view pager
    private fun deleteListenerPagerEvents(){
        if (pagerIsListening) {
            mPager?.removeOnPageChangeListener(this)
            pagerIsListening = false
        }
    }

    //métodos fragment

    //Este método inicializa los fragments principales de para esta funcionalidad para tener su estado guardado
    private fun initPagerFragments(){
        homeMapFragment = HomeMapFragment()
        homeListFragment = EventListFragment()
        pagerFragments.add(homeMapFragment!!)
        pagerFragments.add(homeListFragment!!)
    }

    //Estos dos métodos sirven para volver a la página anterior del pager en caso de presionar el botón atrás
    fun getActualPage(): Int {
        return mPager?.currentItem ?: 0
    }

    fun setPagerPageBackwards() {
        val currentItem = mPager?.currentItem ?: 0
        mPager?.currentItem = currentItem - 1
    }

    //Métodos custom toolbar

    //Este método inicializa la clase
    /**
     * @see toolbarAnimationUtils
     * */
    //Que ese encarga de la animcación dinámica de los elementos del toolbar
    private fun setUpToolbar(){
        toolbarAnimationUtils = ToolbarAnimationManager(activity)

        LinerLayoutHomeToolbar.children.forEach {
            toolbarElementsData.add(it)
        }


        toolbarAnimationUtils?.setToolbarElementsData(toolbarElementsData)

    }

    //Este método inicializa los listener de los diferentes botones del toolbar
    private fun setUpButtons(){
        changeBetweenMapAndListButton.setOnClickListener(changePageButtonListener)
        showMyPositionButton.setOnClickListener(myPositionButtonListener)
    }

    //La función de este método es evitar que la interfaz del toolbar sea erronea respecto a su página actual
    //Como resultado de un cambio en el tab bar mientras se esté realizando un cambio de página
    private fun setToolbarUIAfterPageChange(){
        val currentPage = mPager?.currentItem

        when (currentPage) {
            0 -> {
                run {
                    toolbarAnimationUtils?.changeIconsDinamically(0 ,0.0f)
                }
            }
            1-> {
                run {
                    toolbarAnimationUtils?.changeIconsDinamically(0 ,1.0f)
                }
            }
        }
    }

    private fun requestPermission(){
        val permiso = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permiso == PackageManager.PERMISSION_GRANTED){
            if (!locationListenerActive) {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)
                locationListenerActive = true
            }
            moveToUserPosition()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){

                }
                else{
                    requestPermission()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun moveToUserPosition(){
        Log.i("MARKER","MovetoUser es llamado")
        if (mPager?.currentItem != 0) return

        val permiso = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permiso == PackageManager.PERMISSION_GRANTED){
            locationManager?.let { location ->
                if (location.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.i("MARKER","Position is enabled, continue")
                    val currentLocation = location.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (currentLocation != null){
                        Log.i("MARKER","CurrentLocation is not null, continue")
                        val localizaConUsuario = LatLng(currentLocation.latitude ,currentLocation.longitude)
                        homeMapFragment?.animateCameraToLocation(localizaConUsuario)
                    }
                } else {
                    //Ubicación desactivada
                }
            }
        } else {
            //Ubicación no permitida
        }
    }

    private fun setInitLocationFromGpsToMapIfPosible(){
        if (mPager?.currentItem != 0) return

        val permiso = ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permiso == PackageManager.PERMISSION_GRANTED){
            locationManager?.let { location ->
                if (location.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val currentLocation = location.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (currentLocation != null){
                        val localizaConUsuario = LatLng(currentLocation.latitude ,currentLocation.longitude)
                        homeMapFragment?.setInitialLocationFromGps(localizaConUsuario)
                    }
                } else {
                    //Ubicación desactivada
                }
            }
        } else {
            //Ubicación no permitida
        }
    }

    private fun initLocation(){
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        requestPermission()
    }

}
