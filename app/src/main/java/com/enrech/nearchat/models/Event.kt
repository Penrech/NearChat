package com.enrech.nearchat.models

import com.google.firebase.firestore.GeoPoint
import java.util.*
import kotlin.collections.ArrayList

data class Event(
    var eventID: String?,
    var eventName: String?,
    var eventPhotoBig: String?,
    var eventPhotoThumbnail: String?,
    var eventUsersActive: Int?,
    var maxUserActive: Int?,
    var eventUsersLocations: ArrayList<EventUserLocation>?,
    var eventActiveUsers: ArrayList<String>?,
    var eventUsers: ArrayList<EventUser>?,
    var eventPublicChat: String?,
    var eventFinishDate: Date?,
    var eventLocation: GeoPoint?,
    var eventRange: Int?,
    var adminTicketsChats: ArrayList<EventInfoChat>?,
    var privateAdminChat: PrivateChat?,
    var bloquedUsers: ArrayList<UserSimpleInfo>?,
    var finished: Boolean?
) {
}