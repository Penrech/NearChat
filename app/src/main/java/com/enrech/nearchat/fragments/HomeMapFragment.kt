package com.enrech.nearchat.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

import com.enrech.nearchat.R
import com.enrech.nearchat.interfaces.NotifyInteractionHomeTab
import com.enrech.nearchat.utils.CountriesObject
import com.enrech.nearchat.utils.LocationWithoutGPSUtils
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_home_map.view.*
import java.lang.Exception


//Este fragment contiene la interfaz y parte de la funcionalidad del mapa de eventos cercanos
class HomeMapFragment : SupportMapFragment(), OnMapReadyCallback {

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

            mapFragment = this
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
        Log.i("MARKER","My position markers is : $myPositionMarker, In location $latLng")
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

    fun setInitialLocationFromGps(initalLocation: LatLng){
        initMoveMyPositionUser(latLng = initalLocation)
        this.initialLocationFromGps = initalLocation
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
