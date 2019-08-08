package com.enrech.nearchat.models

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

data class User(
    var userID: String? = null,
    var email: String? = null,
    var username: String? = null,
    var nameSurname: String? = null,
    var bio: String? = null,
    var photoThumbnail: String? = null,
    var photoBig: String? = null,
    var lastKnownPosition: GeoPoint? = null,
    var connectedNow: Boolean? = null,
    @ServerTimestamp var lastConnection: Date? = null,
    var currentEvent: String? = null,
    var privateChats: ArrayList<String>? = arrayListOf(),
    var contacts: ArrayList<String>? = arrayListOf(),
    var bloquedUsers: ArrayList<String>? = arrayListOf(),
    var events: ArrayList<UserEventFormat>? = arrayListOf(),
    var showNotifications: Boolean? = null,
    var showNearEventsNotifications: Boolean? = null,
    var showNewPrivateChatMessagesNotifications: Boolean? = null,
    var showNewEventChatMessagesNotifications: Boolean? = null,
    var showOutOfEventBoundariesNotifications: Boolean? = null,
    var hideConectionStateToUnknowns: Boolean? = null,
    var hidePhotoToUnknowns: Boolean? = null,
    var hideNameToUnknowns: Boolean? = null,
    var showPrivateChatReadMessage: Boolean? = null,
    var accountActivated: Boolean? = null){

    fun getLastKnownPositionInLatLng(): LatLng? {
        return if (lastKnownPosition == null) null else LatLng(lastKnownPosition!!.latitude,lastKnownPosition!!.longitude)
    }

    fun setLastKnowPositionInGeopoint(latLngLastKnowPosition: LatLng) : GeoPoint {
        lastKnownPosition = GeoPoint(latLngLastKnowPosition.latitude,latLngLastKnowPosition.longitude)
        return lastKnownPosition!!
    }

    companion object {

        fun registerNewUser(userID: String?, email: String?,username: String?,photoThumbnail: String?,photoBig: String?): User {
            val newUser = User(userID,
                email,username,null,null,photoThumbnail,photoBig,null,
                true,null,null, arrayListOf(), arrayListOf(), arrayListOf(), arrayListOf(), true,
                true,true,true,true,
                false, false,false,true,true)
            return newUser
        }
    }
}