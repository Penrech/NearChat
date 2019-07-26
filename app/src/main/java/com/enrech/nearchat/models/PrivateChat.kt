package com.enrech.nearchat.models

import com.google.firebase.firestore.Exclude

data class PrivateChat(
    var pChatID: String?,
    var adminEvent: String?,
    var users: ArrayList<PrivateChatUserSubModel>?,
    var messages: ArrayList<PrivateChatMessageSubModel>?)
{

    var isPinned: Boolean? = null
    @Exclude get

    fun isPinned(loggedUser: String): Boolean? {
        if (isPinned != null) return isPinned

       val userInChat = users?.indexOfFirst { it.userID == loggedUser }

        if (userInChat != null && userInChat != -1 ){
            val userRef = users!![userInChat]
            isPinned = userRef.hasThisChatPinned
            return isPinned
        }

        isPinned = false
        return isPinned
    }

    fun setPinned(loggedUser: String, pinned: Boolean) {
        val userInChat = users?.indexOfFirst { it.userID == loggedUser }

        if (userInChat != null && userInChat != -1){
             isPinned = pinned
             users!![userInChat].hasThisChatPinned = isPinned
        }
    }

    fun isCurrentUserBaned(loggedUser: String) : Boolean {
        val userInChat = users?.indexOfFirst { it.userID == loggedUser }

        if (userInChat != null && userInChat != -1){
            val userRef = users!![userInChat]
            return userRef.isBanned ?: false
        }

        return true
    }


}