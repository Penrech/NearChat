package com.enrech.nearchat.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

data class User(
    var userID: String?,
    var email: String?,
    var username: String?,
    var nameSurname: String?,
    var bio: String?,
    var photoThumbnail: String?,
    var photoBig: String?,
    var lastKnownPosition: GeoPoint?,
    var connectedNow: Boolean,
    @ServerTimestamp var lastConnection:  Date?,
    var currentEvent: String?,
    var privateChats: ArrayList<String>?,
    var contacts: ArrayList<String>?,
    var bloquedUsers: ArrayList<String>?,
    var events: ArrayList<String>?,
    var showNotifications: Boolean?,
    var showNearEventsNotifications: Boolean?,
    var showNewPrivateChatMessagesNotifications: Boolean?,
    var showNewEventChatMessagesNotifications: Boolean?,
    var showOutOfEventBoundariesNotifications: Boolean?,
    var hideConectionStateToUnknowns: Boolean?,
    var hidePhotoToUnknowns: Boolean?,
    var hideNameToUnknowns: Boolean?,
    var showPrivateChatReadMessage: Boolean?,
    var accountActivated: Boolean?){

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