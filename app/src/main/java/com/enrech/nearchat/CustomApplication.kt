package com.enrech.nearchat

import android.app.Application
import com.enrech.nearchat.models.Country
import com.enrech.nearchat.utils.CountriesObject.listOfAllCountries
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader


class CustomApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initListOfAllCountriesDataFromJSON()
    }

    private fun initListOfAllCountriesDataFromJSON(){
        val IS = resources.openRawResource(R.raw.country_capitals)
        val reader = BufferedReader(InputStreamReader(IS))
        val gson = Gson()
        val CountriesType = object : TypeToken<ArrayList<Country>>() {}.type
        listOfAllCountries = gson.fromJson(reader,CountriesType)
        reader.close()
        IS.close()
    }
}