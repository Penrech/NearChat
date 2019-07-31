package com.enrech.nearchat.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import java.util.*
import kotlin.collections.ArrayList

data class Event(
    var eventID: String?,
    var eventName: String?,
    var eventPhotoBig: String?,
    var eventPhotoThumbnail: String?,
    var maxUserActive: Int?,
    var eventUsersLocations: ArrayList<EventUserLocation>?,
    var eventActiveUsers: ArrayList<String>?,
    var eventUsers: ArrayList<EventUser>?,
    var eventPublicChat: PublicChat?,
    var eventFinishDate: Date?,
    var eventLocation: GeoPoint?,
    var eventRange: Int?,
    var adminTicketsChats: ArrayList<EventInfoChat>?,
    var privateAdminChat: PrivateChat?,
    var bloquedUsers: ArrayList<UserSimpleInfo>?,
    var finished: Boolean?
) {


    companion object {

        fun newInstance(userID: String, eventID: String, eventName: String, eventRange: Int, eventPosition: LatLng, eventEndDate: Date,
                        eventPhotoBig: String?, eventPhotoThumbnail: String?, maxUserActive: Int?) : Event
        {
            val eventUserLocation = EventUserLocation(userID, GeoPoint(eventPosition.latitude,eventPosition.longitude))
            val eventUser = EventUser.newInstance(userID,true)
            val eventPublicChat = PublicChat("Id", arrayListOf())

            val eventUsersLocations: ArrayList<EventUserLocation> = arrayListOf()
            eventUsersLocations.add(eventUserLocation)
            val eventUsers: ArrayList<EventUser> = arrayListOf()
            eventUsers.add(eventUser)
            val eventActiveUsers: ArrayList<String> = arrayListOf()
            eventActiveUsers.add(userID)

            val privateUserChatModel = PrivateChatUserSubModel("id",userID,false,true,false)
            val privateAdminChat = PrivateChat("id", arrayListOf(privateUserChatModel), arrayListOf())

            val newEvent = Event(eventID,eventName,eventPhotoBig,eventPhotoThumbnail,maxUserActive,eventUsersLocations,eventActiveUsers,
                eventUsers,eventPublicChat,eventEndDate, GeoPoint(eventPosition.latitude, eventPosition.longitude),eventRange,
                arrayListOf(),privateAdminChat, arrayListOf(),false)

            return newEvent
        }
    }
}