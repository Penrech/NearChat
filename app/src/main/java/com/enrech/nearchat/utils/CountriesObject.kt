package com.enrech.nearchat.utils

import com.enrech.nearchat.models.Country
import com.google.android.gms.maps.model.LatLng

object CountriesObject {
    var listOfAllCountries: ArrayList<Country>? = null

    private val defaultLatLng = LatLng(40.4,-3.683333)

    fun getLatLonFromCountryCode(countryCode: String?): LatLng {
        if (countryCode != null) {
            val upperCC = countryCode.toUpperCase()
            val index = listOfAllCountries!!.indexOfFirst { it.CountryCode == upperCC }
            return if (index != -1) {
                val country = listOfAllCountries!![index]
                country.getLatLon()
            } else {
                defaultLatLng
            }
        } else {
            return defaultLatLng
        }
    }
}