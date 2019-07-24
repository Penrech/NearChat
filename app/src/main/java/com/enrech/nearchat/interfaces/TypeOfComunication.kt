package com.enrech.nearchat.interfaces


enum class TypeOfComunication(var type: Int) {
    TALK(0), SHOUT(1), BROADCAST(2);

    companion object {
        private val map = TypeOfComunication.values().associateBy(TypeOfComunication::type)
        fun fromInt(type: Int) = map[type]
    }
}