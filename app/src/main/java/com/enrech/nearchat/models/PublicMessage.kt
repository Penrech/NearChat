package com.enrech.nearchat.models

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class PublicMessage(
    var messageID: String?,
    var adminMessage: Boolean?,
    var sender: String?,
    var username: String?,
    var nameSurname: String?,
    var photoThumbnail: String?,
    var sendLocalTime: Date?,
    @ServerTimestamp var sendeServerTime: Date?,
    var interactionType: String?,
    var messageText: String?,
    var location: GeoPoint?
) {
}