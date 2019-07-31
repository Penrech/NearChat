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

    companion object {

        fun newInstance(userID: String, admin: Boolean) : EventUser {
            val newUser = EventUser(
                userID,
                "TALK",
                false,
                true,
                true,
                true,
                admin,
                false,
                admin,
                arrayListOf()
            )

            return newUser
        }
    }
}