package com.enrech.nearchat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.enrech.nearchat.R
import android.util.Log
import com.enrech.nearchat.utils.CountriesObject
import com.enrech.nearchat.utils.LocationWithoutGPSUtils
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

    private fun turnOnListeners(){}

    private fun turnOffListeners(){}

    fun getInitMapLocation(): LatLng{
        locationWithoutGPSUtils = LocationWithoutGPSUtils()
        val countryCode = locationWithoutGPSUtils?.getDeviceCountryCode(context!!)
        return CountriesObject.getLatLonFromCountryCode(countryCode)
    }

}
