package com.enrech.nearchat.models

data class EventInfoChatUserReported(
    var userID: String?,
    var username: String?,
    var nameSurname: String?,
    var photoThumbnail: String?,
    var messageTextReported: String?,
    var reasonOfReport: String?
) {
}