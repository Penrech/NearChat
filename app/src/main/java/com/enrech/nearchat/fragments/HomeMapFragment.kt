package com.enrech.nearchat.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.NotifyInteractionHomeTab
import com.enrech.nearchat.utils.CountriesObject
import com.enrech.nearchat.utils.LocationWithoutGPSUtils
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*

//Este fragment contiene la interfaz y parte de la funcionalidad del mapa de eventos cercanos
class HomeMapFragment : Fragment(), OnMapReadyCallback {

    //Variables

    private var gMap: GoogleMap? = null

    private var locationWithoutGPSUtils: LocationWithoutGPSUtils? = null

    private var mapFragment: SupportMapFragment? = null

    private var userMarkerDrawableID: Int? = null

    private var initialLocationFromGps: LatLng? = null

    private var myPositionMarker: Marker? = null

    private var notifyInteractionHomeTab: NotifyInteractionHomeTab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userMarkerDrawableID = context!!.resources.getIdentifier("main_user_marker","drawable",context?.packageName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mapFragment == null) {

            mapFragment = childFragmentManager.findFragmentById(R.id.homeMap) as SupportMapFragment
            mapFragment?.getMapAsync(this)
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is NotifyInteractionHomeTab) {
            notifyInteractionHomeTab = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        notifyInteractionHomeTab = null
    }

    override fun onPause() {
        super.onPause()
        turnOffListeners()
    }

    override fun onResume() {
        super.onResume()
        turnOnListeners()
    }

    fun receiveDeepHidePropagation(hide: Boolean) {
        if (hide) {
            turnOffListeners()
        } else {
            turnOnListeners()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            turnOnListeners()
        } else {
            turnOffListeners()
        }
    }

    private fun turnOnListeners(){}

    private fun turnOffListeners(){}

    override fun onMapReady(p0: GoogleMap?) {
        gMap = p0

        val mapSettings = gMap?.uiSettings
        mapSettings?.isMapToolbarEnabled = false
        mapSettings?.isMyLocationButtonEnabled = false
        mapSettings?.isCompassEnabled = false

        if (initialLocationFromGps != null) {
            gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocationFromGps, 18f))
        } else {
            gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(getInitMapLocation(), 18f))
        }

        notifyInteractionHomeTab?.homeMapInitMap()
    }

    fun isGmapInit(): Boolean {
        return gMap != null
    }

    fun animateCameraToLocation(location: LatLng) {
        initMoveMyPositionUser(location)
        gMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location , 18f))
    }

    fun initMoveMyPositionUser(latLng: LatLng){
        if (gMap == null) return

        if (myPositionMarker == null) {
            myPositionMarker = gMap?.addMarker(MarkerOptions()
                .position(latLng)
                .icon(loadAndResizeMarker(userMarkerDrawableID!!,120))
            )
        } else {
            myPositionMarker?.position = latLng
        }
    }

    fun removeMyPositionUserMarker(){
        myPositionMarker?.remove()
        myPositionMarker = null
    }

    private fun getInitMapLocation(): LatLng {
        locationWithoutGPSUtils = LocationWithoutGPSUtils()
        val countryCode = locationWithoutGPSUtils?.getDeviceCountryCode(context!!)
        return CountriesObject.getLatLonFromCountryCode(countryCode)
    }

    private fun getRandomEventMarker(): Drawable {
        val random = (0..15).random()

        val pattern = "event_marker_%d"
        val name = String.format(pattern,random + 1)
        val id = context!!.resources.getIdentifier(name,"drawable",context!!.packageName)
        return context!!.resources.getDrawable(id,context!!.theme)
    }

    private fun loadAndResizeMarker(drawableId: Int, newSize: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context!!, drawableId)
        vectorDrawable!!.setBounds(0, 0, newSize, newSize)
        val bitmap =
            Bitmap.createBitmap(newSize, newSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}
