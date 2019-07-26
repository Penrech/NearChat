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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_map, container, false)
        return view
    }

    override fun onResume() {
        super.onResume()
        turnOnListeners()
    }

    override fun onPause() {
        super.onPause()
        turnOffListeners()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.i("HOMEMAPVISIBLE","event map is visible : $isVisibleToUser")
        if (isVisibleToUser) {
            turnOnListeners()
        } else {
            turnOffListeners()
        }
    }

    fun receiveDeepHidePropagation(hide: Boolean) {
        if (hide) {
            turnOffListeners()
        } else {
            turnOnListeners()
        }
    }

    private fun turnOnListeners(){
        Log.i("PAGERFRAGMENTVISIBLE", "Event Map fragment turn on listeners")
    }

    private fun turnOffListeners(){
        Log.i("PAGERFRAGMENTVISIBLE", "Event Map fragment turn off listeners")
    }

    fun getInitMapLocation(): LatLng{
        locationWithoutGPSUtils = LocationWithoutGPSUtils()
        val countryCode = locationWithoutGPSUtils?.getDeviceCountryCode(context!!)
        return CountriesObject.getLatLonFromCountryCode(countryCode)
    }

}
