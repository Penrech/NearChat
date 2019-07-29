package com.enrech.nearchat.models

data class EventUser(
    var userID: String?,
    var interactionType: String?,
    var showCurrentInteractionRange: Boolean?,
    var showInteractionTalk: Boolean?,
    var showInteractionShout: Boolean?,
    var showInteractionBroadcast: Boolean?,
    var admin: Boolean?,
    var showAdminBadged: Boolean?,
    var fullControl: Boolean?,
    var ticketsChat: ArrayList<String>?
) {
}