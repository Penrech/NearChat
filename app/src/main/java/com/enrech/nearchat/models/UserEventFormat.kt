package com.enrech.nearchat.models

data class UserEventFormat(
    var isPinned: Boolean? = null,
    var isAdmin: Boolean? = null,
    var eventID: String? = null
) {
}