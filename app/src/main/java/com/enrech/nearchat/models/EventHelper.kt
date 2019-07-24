package com.enrech.nearchat.models

import android.graphics.drawable.Drawable

class EventHelper(val type: TypeOFHelper, val description: String, val buttonLabel: String?, val drawable: Drawable?, val hasCloseButton: Boolean) {

    enum class TypeOFHelper(val type: Int){
        NOEVENT(0), EVENTEND(1), LOADINGEVENT(2), CANTENTER(3), TOOFAR(4);

        companion object {
            private val map = TypeOFHelper.values().associateBy(TypeOFHelper::type)
            fun fromInt(type: Int) = map[type]
        }
    }

}