package com.enrech.nearchat.interfaces

interface NotifyInteractionEventTab {
    fun eventOpenAddEditEventClick(isAdd: Boolean)
    fun eventOpenGetLocationEventClick(actualLocation: String?)
    fun eventRecieveLatLngFromMap(latLng: String)
    fun eventPropagateBackButton()
    fun eventChangeChatConversationType(typeOfComunication: TypeOfComunication)
    fun eventPagerLoadedWithCurrentItem(item: Int)
    fun eventClose()
    fun eventLoadEvent()
    fun eventEnd()
    fun eventTooFar()
}