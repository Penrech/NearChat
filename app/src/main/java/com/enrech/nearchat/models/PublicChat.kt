package com.enrech.nearchat.models

data class PublicChat(
    var publicChatID: String?,
    var messages: ArrayList<PublicMessage>?
) {
}