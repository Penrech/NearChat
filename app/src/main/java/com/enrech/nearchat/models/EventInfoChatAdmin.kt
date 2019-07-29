package com.enrech.nearchat.models

data class EventInfoChatAdmin(
    var userID: String?,
    var typing: Boolean?,
    var username: String?,
    var nameSurname: String?,
    var photoThumbnail: String?,
    var inChat: Boolean?
) {
}