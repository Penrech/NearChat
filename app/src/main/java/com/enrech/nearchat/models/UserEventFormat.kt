package com.enrech.nearchat.models

data class UserEventFormat(
    var isPinned: Boolean?,
    var isAdmin: Boolean?,
    var eventID: String?
) {
}