package com.enrech.nearchat.models

import com.google.android.gms.maps.model.LatLng

class Country (val CountryName: String,
               val CapitalName: String,
               val CapitalLatitude: String,
               val CapitalLongitude: String,
               val CountryCode: String,
               val ContinentName: String) {

    fun getLatLon(): LatLng {
        val lat = CapitalLatitude.toDouble()
        val lon = CapitalLongitude.toDouble()
        return LatLng(lat,lon)
    }
}