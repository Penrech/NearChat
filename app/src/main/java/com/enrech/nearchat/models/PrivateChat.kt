package com.enrech.nearchat.models

import com.google.firebase.firestore.Exclude

data class PrivateChat(
    var pChatID: String?,
    var adminEvent: String?,
    var users: ArrayList<PrivateChatUserSubModel>?,
    var messages: ArrayList<PrivateChatMessageSubModel>?)
{

    fun isCurrentUserBaned(loggedUser: String) : Boolean {
        val userInChat = users?.indexOfFirst { it.userID == loggedUser }

        if (userInChat != null && userInChat != -1){
            val userRef = users!![userInChat]
            return userRef.isBanned ?: false
        }

        return true
    }


}