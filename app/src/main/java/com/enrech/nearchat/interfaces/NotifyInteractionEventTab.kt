package com.enrech.nearchat.interfaces

interface NotifyInteractionEventTab {
    fun eventOpenEditEventClick(boolean: Boolean)
    fun eventOpenGetLocationEventClick(boolean: Boolean)
    fun eventRecieveLatLngFromMap(latLng: String)
    fun eventPropagateBackButton()
    fun eventChangeChatConversationType(typeOfComunication: TypeOfComunication)
    fun eventPagerLoadedWithCurrentItem(item: Int)
    fun eventLoadEvent()
    fun eventEnd()
    fun eventTooFar()
}