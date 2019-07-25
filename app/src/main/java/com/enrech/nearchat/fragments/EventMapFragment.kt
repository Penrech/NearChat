package com.enrech.nearchat.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.enrech.nearchat.R
import android.content.Context.TELEPHONY_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.telephony.TelephonyManager
import android.util.Log
import com.enrech.nearchat.CustomApplication
import com.enrech.nearchat.utils.CountriesObject
import com.enrech.nearchat.utils.LocationWithoutGPSUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng


//Este fragment contiene la interfaz y parte de la funcionalidad del mapa del evento
class EventMapFragment : Fragment() {

    //Variables

    private var locationWithoutGPSUtils: LocationWithoutGPSUtils? = null

    private var mapView: MapView? = null
    private var gMap: GoogleMap? = null

    private var mapViewBundle: Bundle? = null

    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    //Listener

    private var onMapReadyCallback = object : OnMapReadyCallback {
        override fun onMapReady(p0: GoogleMap?) {
            gMap = p0

            val mapSettings = gMap?.uiSettings
            mapSettings?.isMapToolbarEnabled = false
            mapSettings?.isMyLocationButtonEnabled = false
            mapSettings?.isCompassEnabled = false

            gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(getInitMapLocation(), 12f))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_map, container, false)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY,mapViewBundle)
        }

        mapView?.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    fun getInitMapLocation(): LatLng{
        locationWithoutGPSUtils = LocationWithoutGPSUtils()
        val countryCode = locationWithoutGPSUtils?.getDeviceCountryCode(context!!)
        return CountriesObject.getLatLonFromCountryCode(countryCode)
    }

}
