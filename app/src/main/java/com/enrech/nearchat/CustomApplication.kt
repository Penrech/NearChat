package com.enrech.nearchat

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.ViewGroup
import com.enrech.nearchat.activities.RootActivity
import com.enrech.nearchat.models.Country
import com.enrech.nearchat.utils.CountriesObject.listOfAllCountries
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

class CustomApplication: Application(), Application.ActivityLifecycleCallbacks{

    var currentActivity: Activity? = null

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

    fun showMessage(message: String) {
        if (currentActivity != null) {

            val view = currentActivity!!.window.decorView.rootView
            val snack = Snackbar.make(view,message, Snackbar.LENGTH_LONG)

            snack.show()
        }

    }

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityDestroyed(p0: Activity) {
        if (currentActivity == p0) currentActivity = null
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {}
}