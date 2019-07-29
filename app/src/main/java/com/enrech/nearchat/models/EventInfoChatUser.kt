package com.enrech.nearchat.models

data class EventInfoChatUser(
    var userID: String?,
    var typing: Boolean?,
    var username: String?,
    var nameSurname: String?,
    var photoThumbnail: String?
) {
}