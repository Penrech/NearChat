package com.enrech.nearchat.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

data class PrivateChatMessageSubModel(
    var messageID: String?,
    var sender: String?,
    var senderUsername: String?,
    var senderNameSurname: String?,
    var senderPhotoThumbnail: String?,
    var sendLocalTime: Date?,
    @ServerTimestamp var sendServerTime: Date?,
    var readers: ArrayList<String>?,
    var read: Boolean?
) {
}