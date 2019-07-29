package com.enrech.nearchat.models

data class EventInfoChat(
    var eventInfoChatID: String?,
    var parentEventID: String?,
    var eventInfoChatUser: EventInfoChatUser?,
    var ticketType: String?,
    var admins: ArrayList<EventInfoChatAdmin>?,
    var userReported: EventInfoChatUserReported?
) {
}