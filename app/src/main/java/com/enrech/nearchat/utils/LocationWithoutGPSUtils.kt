package com.enrech.nearchat.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager

//Esta clase, si bien he hecho alguna modificación, no es propia.
//La utilizo para conseguir el código del pais, para mostrar la ubicación inicial de los diferentes mapas de la app
class LocationWithoutGPSUtils {

    fun getDeviceCountryCode(context: Context): String {
        var countryCode: String?

        // try to get country code from TelephonyManager service
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        if (tm != null) {
            // query first getSimCountryIso()
            countryCode = tm.simCountryIso
            if (countryCode != null && countryCode.length == 2)
                return countryCode.toLowerCase()

            if (tm.phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
                // special case for CDMA Devices
                countryCode = getCDMACountryIso()
            } else {
                // for 3G devices (with SIM) query getNetworkCountryIso()
                countryCode = tm.networkCountryIso
            }

            if (countryCode != null && countryCode.length == 2)
                return countryCode.toLowerCase()
        }

        // if network country not available (tablets maybe), get country code from Locale class
        countryCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0).country
        } else {
            context.resources.configuration.locale.country
        }

        return if (countryCode != null && countryCode.length == 2) countryCode.toUpperCase() else "ES"

        // general fallback to "es"
    }

    @SuppressLint("PrivateApi")
    private fun getCDMACountryIso(): String? {
        try {
            // try to get country code from SystemProperties private class
            val systemProperties = Class.forName("android.os.SystemProperties")
            val get = systemProperties.getMethod("get", String::class.java)

            // get homeOperator that contain MCC + MNC
            val homeOperator = get.invoke(
                systemProperties,
                "ro.cdma.home.operator.numeric"
            ) as String

            // first 3 chars (MCC) from homeOperator represents the country code
            val mcc = Integer.parseInt(homeOperator.substring(0, 3))

            // mapping just countries that actually use CDMA networks
            when (mcc) {
                330 -> return "PR"
                310 -> return "US"
                311 -> return "US"
                312 -> return "US"
                316 -> return "US"
                283 -> return "AM"
                460 -> return "CN"
                455 -> return "MO"
                414 -> return "MM"
                619 -> return "SL"
                450 -> return "KR"
                634 -> return "SD"
                434 -> return "UZ"
                232 -> return "AT"
                204 -> return "NL"
                262 -> return "DE"
                247 -> return "LV"
                255 -> return "UA"
            }
        } catch (ignored: ClassNotFoundException) {
        } catch (ignored: NoSuchMethodException) {
        } catch (ignored: IllegalAccessException) {
        } catch (ignored: NullPointerException) {
        }

        return null
    }
}