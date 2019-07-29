package com.enrech.nearchat.models

data class PrivateChatUserSubModel(
    var documentID: String?,
    var userID: String?,
    var typing: Boolean?,
    var hasReferenceToThisChat: Boolean?,
    var isBanned: Boolean?
) {
}